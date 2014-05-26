package com.fav24.dataservices.service.generic.hook;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import org.apache.commons.io.IOUtils;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.exception.ServerException;


/**
 * Esta clase valida, compila y carga puntos de inserción para el servicio genérico. 
 */
public class GenericServiceHookLoader {

	public static final String ERROR_HOOK_SOURCE_ACCESS = "H001";
	public static final String ERROR_HOOK_SOURCE_ACCESS_MESSAGE = "No ha sido posible acceder normalmente a alguno de los fuentes de los hooks: <%s>.";
	public static final String ERROR_HOOK_CLASS_NOT_FOUND = "H002";
	public static final String ERROR_HOOK_CLASS_NOT_FOUND_MESSAGE = "No ha sido posible localizar la clase <%s>.";
	public static final String ERROR_HOOK_CLASS_INSTANCE = "H003";
	public static final String ERROR_HOOK_CLASS_INSTANCE_MESSAGE = "No ha sido posible crear una instancia de la clase <%s>.";
	public static final String ERROR_HOOK_DIAGNOSTIC = "H004";
	public static final String ERROR_HOOK_DIAGNOSTIC_MESSAGE = "Se ha producido un problema durante la construcción del diagnóstico por parte del compilador para el fichero <%s>.";


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
	 * 
	 * @throws IOException
	 */
	public static Map<String, StringBuilder> compile(File[] files) throws ServerException {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, DataServicesContext.DEFAULT_CHARSET);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));

		compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits).call();

		Map<String, StringBuilder> messages = getHTMLMessages(diagnostics);

		try {

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
	public static Map<String, StringBuilder> compile(URL[] urls) throws ServerException {
	
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
	public static AbstractList<GenericServiceHook> load(URL[] urls) throws ServerException {

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
				result.append(diagnostic.getSource().getName()).append("<br/>");
			}

			result.append(getProblemHTML(diagnostic));
		}

		return messages;
	}
}
