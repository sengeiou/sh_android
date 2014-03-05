package com.fav24.dataservices.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


/**
 * Objecto de transferencia correspondiente a un fichero a cargar.
 * 
 * @author Fav24
 *
 */
public class UploadFilesDto {

	private List<MultipartFile> files;
     
     
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
}