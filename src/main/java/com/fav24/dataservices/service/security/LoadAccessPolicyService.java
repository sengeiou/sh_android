package com.fav24.dataservices.service.security;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;


/**
 * Interfaz de servicio de carga de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface LoadAccessPolicyService {

	/**
	 * Retorna la URL cargada. 
	 * 
	 * @param accessPolicyFile Políticas de acceso a cargar.
	 *  
	 * @return la URL cargada.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicy loadAccessPolicy(AccessPolicy accessPolicyFile) throws ServerException;
}
