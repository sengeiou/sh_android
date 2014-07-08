package com.fav24.dataservices.service.fileSystem;

import java.nio.file.Path;
import java.util.AbstractList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Interfaz de servicio FileSystem. 
 */
public interface FileSystemService {

	public static final Logger logger = LoggerFactory.getLogger(FileSystemService.class);
	
	
	/**
	 * Retorna una lista con el conjunto de ficheros contenidos en la ruta indicada, que cumplen con el 
	 * patrón indicado, y se corresponden con el parámetro @param directoriesOnly. 
	 * 
	 * @param path Ruta de la que se desea obtener la información.
	 * @param pattern Patrón que deben cumplir los archivos contenidos en la lista.
	 * @param directoriesOnly True o false en función de si se desean únicamente directorios o no.
	 * @param filesOnly True o false en función de si se desean únicamente ficheros o no.
	 * 
	 * @return una lista con el conjunto de ficheros contenidos en la ruta indicada, que cumplen con el 
	 * patrón indicado, y se corresponden con el parámetro @param directoriesOnly.
	 */
	public AbstractList<FileInformation> getFileInformationList(Path path, String pattern, boolean directoriesOnly, boolean filesOnly);
}