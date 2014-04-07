package com.fav24.dataservices.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.AbstractList;
import java.util.ArrayList;

/**
 * Clase de utilidades de ficheros.
 */
public class FileUtils {

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

		return files;
	}
}
