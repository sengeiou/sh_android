package com.fav24.dataservices.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


/**
 * Objecto de transferencia correspondiente a un fichero a cargar.
 */
public class UploadFilesDto {

	private List<MultipartFile> files;
	private List<Boolean> filesAsDefault;
     
     
     /**
      * Retorna el conjunto de ficheros a cargar.
      * 
      * @return el conjunto de ficheros a cargar.
      */
     public List<MultipartFile> getFiles() {
    	 return files;
     }
     
     /**
      * Asigna el conjunto de ficheros a cargar.
      * 
      * @param files La lista de los contenidos de los ficheros.
      */
     public void setFiles(List<MultipartFile> files) {
    	 this.files = files;
     }
     
     
     /**
      * Retorna una lista asociada al conjunto de ficheros a cargar, que indica
      * si el fichero deberá ser guardado como por defecto.
      * 
      * Nota: Si existía uno anterior como por defecto, será renombrado a .bak y sustituido por el nuevo.
      * 
      * @return una lista asociada al conjunto de ficheros a cargar, que indica
      * si el fichero deberá ser guardado como por defecto.
      */
     public List<Boolean> getFilesAsDefault() {
    	 return filesAsDefault;
     }
     
     /**
      * Asigna la lista asociada al conjunto de ficheros a cargar, que indica
      * si el fichero deberá ser guardado como por defecto.
      * 
      * @param filesAsDefault La lista asociada al conjunto de ficheros a cargar.
      */
     public void setFilesAsDefault(List<Boolean> filesAsDefault) {
    	 this.filesAsDefault = filesAsDefault;
     }
}