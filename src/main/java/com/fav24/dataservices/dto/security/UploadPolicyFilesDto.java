package com.fav24.dataservices.dto.security;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;


/**
 * Objecto de transferencia correspondiente a un fichero de políticas de acceso a cargar.
 * 
 * @author Fav24
 *
 */
public class UploadPolicyFilesDto {

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
      * Asigna el conjunto de fichero de políticas de acceso.
      * 
      * @param files La lista de los contenidos de los ficheros de políticas a cargar.
      */
     public void setFiles(List<MultipartFile> files) {
    	 this.files = files;
     }
}