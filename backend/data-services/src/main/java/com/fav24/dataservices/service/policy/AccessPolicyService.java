package com.fav24.dataservices.service.policy;



/**
 * Interfaz para servicios relacionados con las políticas de acceso. 
 */
public interface AccessPolicyService {

	public static final String ERROR_LOADING_POLICY_FILES = "PS000";
	public static final String ERROR_LOADING_POLICY_FILES_MESSAGE = "No se indicó ninguna URL de ningún fichero de definición de políticas de acceso.";
	public static final String ERROR_INVALID_POLICY_FILE_URL = "PS001";
	public static final String ERROR_INVALID_POLICY_FILE_URL_MESSAGE = "La URL <%s> no se corresponde con ningún fichero de definición de políticas de acceso.";
	public static final String ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD = "PS002";
	public static final String ERROR_NO_DEFAULT_POLICY_FILES_TO_LOAD_MESSAGE = "Existe ningún fichero de definición de políticas de acceso por defecto.";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED = "PS003";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED_MESSAGE = "No se han definido políticas de acceso.";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED_FOR_ENTITY = "PS004";
	public static final String ERROR_NO_CURRENT_POLICY_DEFINED_FOR_ENTITY_MESSAGE = "No se han definido políticas de acceso para la entidad <%s>.";
	public static final String ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED = "PS005";
	public static final String ERROR_ENTITY_ATTRIBUTES_NOT_ALLOWED_MESSAGE = "La entidad <%s> no tiene definido(s) los atributo(s) <%s> como accesible(s).";
}
