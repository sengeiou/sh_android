package com.fav24.dataservices.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.web.multipart.MultipartFile;

import com.fav24.dataservices.DataServicesContext;

/**
 * Clase de utilidades de ficheros.
 */
public class FileUtils {

	public static final String REPLACED_FILES_SUFFIX = ".bak";

	/**
	 * Retorna un conjunto de referencias a ficheros terminados con el sufijo indicado.
	 * Los ficheros se obtienen realizando una búsqueda en profundidad, a partir del 
	 * directorio indicado.  
	 * 
	 * @param directory Directorio base de donde parte la búsqueda. 
	 * @param suffix Sufijo de los ficheros a localizar.
	 * @param files Lista en donde se almacenan los ficheros encontrados. Puede ser <code>null</code>.
	 * 
	 * @return un conjunto de referencias a ficheros terminados con el sufijo indicado.
	 */
	public static AbstractList<File> getFilesWithSuffix(String directory, final String suffix, AbstractList<File> files) {

		if (files == null) {
			files = new ArrayList<File>();
		}

		File baseDirectory = new File(directory);

		if (baseDirectory.exists() && baseDirectory.isDirectory()) {

			FilenameFilter fileNameFilter = new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {

					if (name.endsWith(suffix)) {
						return true;
					}

					return false;
				}
			};

			for(String fileName : baseDirectory.list()) {

				File file = new File(baseDirectory, fileName);

				if (file.isDirectory()) {
					getFilesWithSuffix(file.getAbsolutePath(), suffix, files);
				}
				else if (fileNameFilter.accept(baseDirectory, fileName)) {
					files.add(file);
				}
			}
		}

		Collections.sort(files, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {

				if (o1 == o2) {
					return 0;
				}

				if (o1 != null && o2 != null) {

					return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
				}

				if (o1 != null) {
					return 1;
				}

				if (o2 != null) {
					return -1;
				}

				return 0;
			}});

		return files;
	}

	/**
	 * Retorna el stream del fichero procedente de un envío, y sustituye el primero existente encontrado en la lista procedente
	 * de la ubicación base de la aplicación.
	 * 
	 * @param multipartFile Fichero procendete del envío web.
	 * @param relativeLocation Ubicación relativa al Home de la aplicación. 
	 * 
	 * @return el fichero procedente de un envío, y sustituye el primero existente encontrado en la lista procedente
	 * de la ubicación base de la aplicación.
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static File createOrReplaceExistingFile(MultipartFile multipartFile, String relativeLocation) throws IllegalStateException, IOException {

		String fileName = multipartFile.getOriginalFilename();

		String location = DataServicesContext.getCurrentDataServicesContext().getApplicationHome() + File.separator + relativeLocation;

		AbstractList<File> defaultFiles = FileUtils.getFilesWithSuffix(location, fileName, null);

		for(File file : defaultFiles) {

			if (fileName.equals(file.getName())) {

				File newName = new File(file.getAbsolutePath() + REPLACED_FILES_SUFFIX);

				if (newName.exists()) {
					newName.delete();
				}

				File newFile = new File(file.getAbsolutePath());

				file.renameTo(newName); 

				try {
					multipartFile.transferTo(newFile);

					return newFile;
				}
				catch (IllegalStateException | IOException e) {

					// En caso de error, se garantiza que el fichero preexistente, queda como estaba.
					newName.renameTo(newFile);

					throw e;
				}
			}
		}

		File newFile = new File(location + File.separator + fileName);
		multipartFile.transferTo(newFile);

		return newFile;
	}

	/**
	 * Retorna el separador de ficheros del sistema.
	 * 
	 * @return el separador de ficheros del sistema.
	 */
	public static String getFileSeparator() {
		return File.separator;
	}

	/**
	 * Retorna la cadena de texto con los caracteres propios de HTML escapados. 
	 * 
	 * @param text Texto a escapar.
	 * 
	 * @return la cadena de texto con los caracteres propios de HTML escapados.
	 */
	public static String scapeHTML(String text) {

		try {

			if (text != null) {

				text = text.replace("Á","&Aacute;");
				text = text.replace("É","&Eacute;");
				text = text.replace("Í","&Iacute;");
				text = text.replace("Ó","&Oacute;");
				text = text.replace("Ú","&Uacute;");
				text = text.replace("á","&aacute;");
				text = text.replace("é","&eacute;");
				text = text.replace("í","&iacute;");
				text = text.replace("ó","&oacute;");
				text = text.replace("ú","&uacute;");

				text = text.replace("À","&Agrave;");
				text = text.replace("È","&Egrave;");
				text = text.replace("Ì","&Igrave;");
				text = text.replace("Ò","&Ograve;");
				text = text.replace("Ù","&Ugrave;");
				text = text.replace("à","&agrave;");
				text = text.replace("è","&egrave;");
				text = text.replace("ì","&igrave;");
				text = text.replace("ò","&ograve;");
				text = text.replace("ù","&ugrave;");

				text = text.replace("€","&euro;");
				text = text.replace("¥","&yen;");
				text = text.replace("£","&pound;");
				text = text.replace("¢","&cent;");

				text = text.replace("&","&amp;");

				text = text.replace("<","&lt;");
				text = text.replace(">","&gt;");

				text = text.replace("©","&copy;");
				text = text.replace("®","&reg;");

				text = text.replace("µ","&micro;");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;
	}

	/**
	 * Retorna el contenido de la URL especificada en forma de cadena de texto.
	 * 
	 * Tan solo funciona para contenidos de tipo texto.
	 * 
	 * @param url URL de la que se desea obtener el contenido.
	 * 
	 * @return el contenido de la URL especificada en forma de cadena de texto.
	 */
	public static String getURLContent(URL url) {

		StringBuilder sourceCodeLines = null;

		try {

			if (url != null) {

				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

				String line;
				while((line = reader.readLine()) != null) {

					if (sourceCodeLines == null) {
						sourceCodeLines = new StringBuilder();
					}
					else {
						sourceCodeLines.append('\n');
					}
					sourceCodeLines.append(line);
				}
				reader.close();
				sourceCodeLines.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sourceCodeLines == null ? null : sourceCodeLines.toString();
	}

	/**
	 * Retorna el contenido de la URL especificada en forma de cadena de texto.
	 * 
	 * Tan solo funciona para contenidos de tipo texto.
	 * 
	 * @param url URL de la que se desea obtener el contenido.
	 * 
	 * @return el contenido de la URL especificada en forma de cadena de texto.
	 */
	public static String getURLContent(String url) {

		try {

			return getURLContent(new URL(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
