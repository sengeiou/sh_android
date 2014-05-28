package com.fav24.dataservices.service.hook.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.service.hook.HookConfigurationService;
import com.fav24.dataservices.util.FileUtils;


/**
 * Implementación del servicio de carga y consulta de puntos de inserción (Hooks). 
 */
@Scope("singleton")
@Component
public class HookConfigurationServiceImpl implements HookConfigurationService {

	@Autowired
	private WebApplicationContext webApplicationContext;
	private NavigableMap<String, GenericServiceHook> hooks;


	/**
	 * Constructor por defecto.
	 */
	public HookConfigurationServiceImpl() {

		this.hooks = new TreeMap<String, GenericServiceHook>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Map<String, StringBuilder> loadDefaultHooks() throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();

		// Se cargan los archivos de políticas de seguridad existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			AbstractList<File> hookSourceFiles = FileUtils.getFilesWithSuffix(applicationHome + "/" + HOOK_SOURCE_RELATIVE_LOCATION, HOOK_SOURCE_SUFFIX, null);

			if (hookSourceFiles.size() == 0) {

				throw new ServerException(ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD, ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD_MESSAGE);
			}
			else {

				URL urls[] = new URL[hookSourceFiles.size()];
				int i=0;
				for(File hookSourceFile : hookSourceFiles) {

					try {

						urls[i++] = hookSourceFile.toURI().toURL();
					} 
					catch (MalformedURLException e) {
						
						throw new ServerException(ERROR_INVALID_HOOK_FILE_URL, String.format(ERROR_INVALID_HOOK_FILE_URL_MESSAGE, hookSourceFile.toURI().toString()));
					}
				}

				return loadHooks(urls);
			}
		}
		else {

			throw new ServerException(DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
	}

	/**
	 * {@inheritDocs}
	 */
	@Override
	public synchronized Map<String, StringBuilder> loadHooks(RemoteFiles hookFiles) throws ServerException {

		Map<String, StringBuilder> diagnostics = null;

		if (hookFiles == null || hookFiles.getURLs() == null || hookFiles.getURLs().length == 0) {

			diagnostics = loadDefaultHooks();
		}
		else {

			diagnostics = loadHooks(hookFiles.getURLs());
		}

		return diagnostics;
	}

	/**
	 * {@inheritDocs}
	 */
	private Map<String, StringBuilder> loadHooks(URL[] sources) throws ServerException {

		if (sources != null && sources.length > 0) {

			Map<String, StringBuilder> compilerDiagnostics = compile(sources);

			if (compilerDiagnostics != null && compilerDiagnostics.size() > 0) {

				return compilerDiagnostics;
			}

			AbstractList<GenericServiceHook> loadedHooks = load(sources);

			for(GenericServiceHook loadedHook : loadedHooks) {

				hooks.put(loadedHook.getAlias(), loadedHook);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropHooks() throws ServerException {

		hooks.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized NavigableMap<String, GenericServiceHook> getAvailableHooks() {

		return hooks;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized GenericServiceHook getHook(String alias) {

		return hooks.get(alias);
	}

	/**
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String getApplicationFolder() throws UnsupportedEncodingException {
		
		String path = this.getClass().getClassLoader().getResource("").getPath();
		String fullPath = URLDecoder.decode(path, "UTF-8");
		String pathArr[] = fullPath.split("/WEB-INF/classes/");
		System.out.println(fullPath);
		System.out.println(pathArr[0]);
		fullPath = pathArr[0];
		
		return fullPath;
	}
	
	/**
	 * Retorna el classpath a usar por el compilador.
	 * 
	 * @return el classpath a usar por el compilador.
	 * 
	 * @throws ServerException 
	 */
	private String getClassPath() throws ServerException {

		StringBuilder path = new StringBuilder();

		path.append(".");
		
		String classPathProperty = System.getProperty("java.class.path");
		if (classPathProperty != null) {
			path.append(File.pathSeparator).append(classPathProperty);
		}

		URL classLocation = this.getClass().getProtectionDomain().getCodeSource().getLocation();
		path.append(File.pathSeparator).append(classLocation.getPath());
		
		URL classesLocation = this.getClass().getClassLoader().getResource("/");

		if (classesLocation != null) {
			
			try {

				String classesLocationPath = classesLocation.getPath();
				
				path.append(File.pathSeparator).append(classesLocationPath);
				
				String applicationFolder = getApplicationFolder();
				String applicationFolderName = webApplicationContext.getApplicationName();
				
				String libsLocationPath = classesLocationPath + "../lib";

				File libsLocation = new File(libsLocationPath);

				if (libsLocation.exists() == false) {
					libsLocationPath = URLDecoder.decode(classesLocationPath + "../" + applicationFolderName + "/WEB-INF/lib/", DataServicesContext.DEFAULT_ENCODING);
					libsLocation = new File(libsLocationPath);
				}

				File[] filesInLibraryPath = libsLocation.listFiles(new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return name.endsWith(".jar");
					}
				});

				if (filesInLibraryPath != null) {

					for (File libraryFile : filesInLibraryPath) {
						libsLocationPath += File.pathSeparator + URLDecoder.decode(libraryFile.getAbsolutePath(), DataServicesContext.DEFAULT_ENCODING);
					}
				}

				path.append(File.pathSeparator).append(libsLocationPath);

				return URLDecoder.decode(path.toString(), DataServicesContext.DEFAULT_ENCODING);
				
			} catch (UnsupportedEncodingException e) {
				
				throw new ServerException(ERROR_INVALID_HOOK_CLASSPATH_URL, String.format(ERROR_INVALID_HOOK_CLASSPATH_URL_STRING, path.toString()));
			}
		}

		return path.toString();
	}

	/**
	 * Compila el conjunto de ficheros suministrados por parámetro.
	 * 
	 * @param files Conjunto de ficheros a compilar.
	 * 
	 * Nota: La compilación es válida únicamente cuando el mapa de salida está vacío.
	 * 
	 * @return un mapa con el resultados de las compilaciones. 
	 * 
	 * @throws ServerException 
	 */
	private Map<String, StringBuilder> compile(File[] files) throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();
		Map<String, StringBuilder> messages = null;

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnosticListener, null, DataServicesContext.DEFAULT_CHARSET);

		try {

			File binaryDirectory = new File(applicationHome + "/" + HOOK_BINARY_RELATIVE_LOCATION);

			if (!binaryDirectory.exists()) {
				binaryDirectory.mkdirs();
			}

			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(binaryDirectory));

			Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));

			List<String> options = new ArrayList<String>();
			
			// Se asigna al compilador el mismo classpath usado en runtime.
			options.addAll(Arrays.asList("-classpath", "/Users/jmvera/development/workspaces/backend/data-services/target/data-services/WEB-INF/classes/"));
//			options.addAll(Arrays.asList("-classpath", getClassPath()));

			JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnosticListener, options, null, compilationUnits);

			if (!compilationTask.call()) {

				messages = getHTMLMessages(diagnosticListener);
			}

			fileManager.close();

		} catch (IOException e) {

			throw new ServerException(ERROR_HOOK_SOURCE_ACCESS, String.format(ERROR_HOOK_SOURCE_ACCESS_MESSAGE, e.getMessage()));
		}

		return messages;
	}

	/**
	 * Compila el conjunto de ficheros suministrados por parámetro.
	 * 
	 * @param urls Conjunto de ficheros a compilar.
	 * 
	 * Nota: La compilación es válida únicamente cuando el mapa de salida está vacío.
	 * 
	 * @return un mapa con el resultados de las compilaciones. 
	 * 
	 * @throws ServerException 
	 * 
	 * @throws IOException
	 */
	private Map<String, StringBuilder> compile(URL[] urls) throws ServerException {

		File[] files = new File[urls.length];

		for(int i=0; i<urls.length; i++) {

			files[i] = new File(urls[i].getFile());
		}

		return compile(files);
	}


	/**
	 * Carga el conjunto de fuentes compilados.
	 * 
	 * @param urls Conjunto de ficheros compilados a cargar.
	 * 
	 * @return una lista de instancias, una for fuente compilado.
	 */
	private static AbstractList<GenericServiceHook> load(URL[] urls) throws ServerException {

		AbstractList<GenericServiceHook> result = new ArrayList<GenericServiceHook>();

		URLClassLoader classLoader = URLClassLoader.newInstance(urls);

		for (URL url : urls) {

			String className = url.getFile();

			className = className.substring(className.lastIndexOf(File.separatorChar) + 1);
			className = className.substring(0, className.lastIndexOf('.'));

			Class<?> hookClass;
			try {
				hookClass = Class.forName(className, true, classLoader);
			} catch (ClassNotFoundException e) {

				throw new ServerException(ERROR_HOOK_CLASS_NOT_FOUND, String.format(ERROR_HOOK_CLASS_NOT_FOUND_MESSAGE, className));
			}

			try {
				result.add((GenericServiceHook) hookClass.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {

				throw new ServerException(ERROR_HOOK_CLASS_INSTANCE, String.format(ERROR_HOOK_CLASS_INSTANCE_MESSAGE, className));
			}
		}

		return result;
	}

	/**
	 * Retorna una cadena de texto con el diagnóstico de los posibles problemas detectados durante la compilación.
	 * 
	 * @param diagnostic Diagnóstico a convertir en cadena HTML.
	 * 
	 * @return una cadena de texto con el diagnóstico de los posibles problemas detectados durante la compilación.
	 * 
	 * @throws ServerException 
	 */
	private static StringBuilder getProblemHTML(Diagnostic<? extends JavaFileObject> diagnostic) throws ServerException {

		AbstractList<String> readenLines = new ArrayList<String>();
		StringBuilder result = new StringBuilder();

		result.append("<p>");
		switch(diagnostic.getKind()) {
		case ERROR:
			result.append("<font color='red'>");
			result.append("ERROR");
			result.append("</font>");
			break;
		case WARNING:
			result.append("<font color='yellow'>");
			result.append("WARNING");
			result.append("</font>");
			break;
		case MANDATORY_WARNING:
		case OTHER:
		case NOTE:
		default:
			break;
		}
		result.append(" l&iacute;nea ").append("<font color='blue'>").append(diagnostic.getLineNumber()).append("</font>");
		result.append(", columna ").append("<font color='blue'>").append(diagnostic.getColumnNumber()).append("</font>").append("<br/>");
		
		result.append("diagn&oacute;stico: ").append("<font color='blue'>").append(diagnostic.getMessage(null)).append("</font>").append("<br/>");

		if (diagnostic.getSource() != null && diagnostic.getPosition() != Diagnostic.NOPOS) {

			BufferedReader lineReader = null;
			try {

				Reader reader = diagnostic.getSource().openReader(true);
				lineReader = new BufferedReader(reader);
				String currentTextLine;
				int currentLine = 0;

				while((currentTextLine = lineReader.readLine()) != null) {

					readenLines.add(currentTextLine);
					currentLine++;

					if (currentLine == diagnostic.getLineNumber()) {
						result.append(currentTextLine.substring(0, (int)diagnostic.getColumnNumber()-1));

						result.append("<font color='red'>");
						result.append("&#8224;");
						result.append("</font>");

						result.append(currentTextLine.substring((int)diagnostic.getColumnNumber()-1));

					}
				}
			}
			catch(Throwable t) {
				throw new ServerException(ERROR_HOOK_DIAGNOSTIC, String.format(ERROR_HOOK_DIAGNOSTIC_MESSAGE, diagnostic.getSource().getName()));
			}
			finally {
				IOUtils.closeQuietly(lineReader);
			}
		}

		result.append("<p>");

		return result;
	}

	/**
	 * Retorna un conjunto de párrafos en HTML, uno por fuente compilado, con los diagnósticos del compilador en caso de haber encontrado problemas.
	 * 
	 * @param diagnostics Conjunto de diagnóticos a formatear.
	 * 
	 * @return un conjunto de párrafos en HTML, uno por fuente compilado, con los diagnósticos.
	 * 
	 * @throws ServerException
	 */
	private static Map<String, StringBuilder> getHTMLMessages(DiagnosticCollector<JavaFileObject> diagnostics) throws ServerException {

		Map<String, StringBuilder> messages = new LinkedHashMap<String, StringBuilder>();

		for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {

			StringBuilder result = messages.get(diagnostic.getSource().getName());

			if (result == null) {

				result = new StringBuilder();
				messages.put(diagnostic.getSource().getName(), result);
			}

			result.append(getProblemHTML(diagnostic));
		}

		return messages;
	}
}
