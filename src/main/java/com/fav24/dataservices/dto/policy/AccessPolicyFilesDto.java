package com.fav24.dataservices.dto.policy;

import com.fav24.dataservices.dto.BaseDto;
import com.fav24.dataservices.exception.ServerException;


/**
 * Clase que contiene la estructura de una petición de carga 
 * de un fichero de definición de políticas de acceso.
 * 
 * @author Fav24
 */
public class AccessPolicyFilesDto extends BaseDto {

	private static final long serialVersionUID = 2649617444051699918L;

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
