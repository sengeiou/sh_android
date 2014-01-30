package com.fav24.dataservices.service.security;



/**
 * Interfaz para servicios relacionados con las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface AccessPolicyService {

	public static final String ERROR_LOADING_POLICY_FILES = "PS000";
	public static final String ERROR_LOADING_POLICY_FILES_MESSAGE = "No se indicó ninguna URL de ningún fichero de definición de políticas de acceso.";
	public static final String ERROR_INVALID_URL = "PS001";
	public static final String ERROR_INVALID_URL_MESSAGE = "La URL %s no es válida.";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED = "PS002";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE = "No se han definido políticas de acceso.";

}
