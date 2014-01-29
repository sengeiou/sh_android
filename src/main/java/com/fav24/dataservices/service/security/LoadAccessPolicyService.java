package com.fav24.dataservices.service.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicyFiles;


/**
 * Interfaz de servicio de carga de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface LoadAccessPolicyService {

	public static final Logger logger = LoggerFactory.getLogger(LoadAccessPolicyService.class);
	
	public static final String ERROR_LOADING_POLICY_FILES = "PS000";
	public static final String ERROR_INVALID_URL = "PS001";
	
	/**
	 * Retorna la URL cargada. 
	 * 
	 * @param accessPolicyFile Políticas de acceso a cargar.
	 *  
	 * @return la URL cargada.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFile) throws ServerException;
}
