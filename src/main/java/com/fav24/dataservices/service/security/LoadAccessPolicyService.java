package com.fav24.dataservices.service.security;

import com.fav24.dataservices.dto.security.AccessPolicyFileResultDto;
import com.fav24.dataservices.dto.security.AccessPolicyFileDto;


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
	 */
	public AccessPolicyFileResultDto loadAccessPolicy(AccessPolicyFileDto accessPolicyFile);
}
