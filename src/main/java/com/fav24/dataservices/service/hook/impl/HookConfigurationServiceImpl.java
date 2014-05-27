package com.fav24.dataservices.service.hook.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.hook.GenericServiceHook;
import com.fav24.dataservices.service.hook.HookConfigurationService;
import com.fav24.dataservices.service.security.AccessPolicyService;
import com.fav24.dataservices.util.FileUtils;


/**
 * Implementación del servicio de carga y consulta de puntos de inserción (Hooks). 
 */
@Scope("singleton")
@Component
public class HookConfigurationServiceImpl implements HookConfigurationService {

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

				throw new ServerException(AccessPolicyService.ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD, 
						AccessPolicyService.ERROR_NO_DEFAULT_HOOK_FILES_TO_LOAD_MESSAGE);
			}
			else {

				URL urls[] = new URL[hookSourceFiles.size()];
				int i=0;
				for(File hookSourceFile : hookSourceFiles) {

					try {

						urls[i++] = hookSourceFile.toURI().toURL();
					} 
					catch (MalformedURLException e) {
						throw new ServerException(AccessPolicyService.ERROR_INVALID_HOOK_FILE_URL, 
								String.format(AccessPolicyService.ERROR_INVALID_HOOK_FILE_URL_MESSAGE, hookSourceFile.toURI().toString()));
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
	private static Map<String, StringBuilder> compile(File[] files) throws ServerException {

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
			options.addAll(Arrays.asList("-classpath", System.getProperty("java.class.path")));

			JavaCompiler.CompilationTask compilationTask = compiler.getTask(null, fileManager, diagnosticListener, null, null, compilationUnits);

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
	private static Map<String, StringBuilder> compile(URL[] urls) throws ServerException {

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
		result.append("L&iacute;nea ").append("<font color='blue'>").append(diagnostic.getLineNumber()).append("</font>");
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
						currentTextLine = currentTextLine.replace("\t", "    ");
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
