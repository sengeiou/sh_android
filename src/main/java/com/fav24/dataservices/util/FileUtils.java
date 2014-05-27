package com.fav24.dataservices.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
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
	 * 
	 * @return el fichero procedente de un envío, y sustituye el primero existente encontrado en la lista procedente
	 * de la ubicación base de la aplicación.
	 * 
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static File createOrReplaceExistingFile(MultipartFile multipartFile) throws IllegalStateException, IOException {

		String fileName = multipartFile.getOriginalFilename();

		AbstractList<File> defaultFiles = FileUtils.getFilesWithSuffix(DataServicesContext.getCurrentDataServicesContext().getApplicationHome(), 
				fileName, null);

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

		File newFile = new File(DataServicesContext.getCurrentDataServicesContext().getApplicationHome() + "/" + fileName);
		multipartFile.transferTo(newFile);

		return newFile;
	}
}
