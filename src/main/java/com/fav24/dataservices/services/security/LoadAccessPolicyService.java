package com.fav24.dataservices.services.security;

import com.fav24.dataservices.to.security.AccessPolicyFileResultTO;
import com.fav24.dataservices.to.security.AccessPolicyFileTO;


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
	public AccessPolicyFileResultTO loadAccessPolicy(AccessPolicyFileTO accessPolicyFile);
}
