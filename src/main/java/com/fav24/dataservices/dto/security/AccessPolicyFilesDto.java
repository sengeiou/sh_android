package com.fav24.dataservices.dto.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de una petición de carga 
 * de un fichero de definición de políticas de acceso.
 * 
 * @author Fav24
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class AccessPolicyFilesDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

	@JsonInclude(Include.NON_NULL)
	private String[] policyFiles;


	/**
	 * Constructor por defecto.
	 */
	public AccessPolicyFilesDto() {
		policyFiles = null;
	}

	/**
	 * Constructor para el retorno de un error. 
	 *  
	 * @param e Excepción a notificar en la respuesta.
	 */
	public AccessPolicyFilesDto(ServerException e) {
		super(e);
	}

	/**
	 * Retorna las urls de los ficheros de definición de políticas de acceso.
	 * 
	 * @return las urls de los ficheros de definición de políticas de acceso.
	 */
	public String[] getPolicyFilesURLs() {
		return policyFiles;
	}

	/**
	 * Asigna las urls de los ficheros de definición de políticas de acceso.
	 * 
	 * @param urls Las urls a asignar.
	 */
	public void setPolicyFilesURLs(String[] urls) {
		this.policyFiles = urls;
	}
}
