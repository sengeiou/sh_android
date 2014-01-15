package com.fav24.dataservices.services;

import java.util.AbstractList;

import com.fav24.dataservices.domain.EntityAccessPolicy;
import com.fav24.dataservices.to.AccessPolicyResultTO;
import com.fav24.dataservices.to.AccessPolicyTO;


/**
 * Interfaz de servicio de consulta de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface AccessPolicyService {

	/**
	 * Retorna el detalle de las políticas de acceso de las entidades. 
	 * 
	 * @param accessPolicy Políticas de acceso a completar.
	 *  
	 * @return el detalle de las políticas de acceso de las entidades indicadas.
	 */
	public AccessPolicyResultTO getAccessPolicy(AccessPolicyTO accessPolicy);
	
	/**
	 * Retorna la lista de entidades accesible desde el exterior.
	 * 
	 * @return la lista de entidades accesible desde el exterior.
	 */
	public AbstractList<String> getPublicEntities();
	
	/**
	 * Retorna el detalle de las políticas de acceso de la entidad indicada.
	 * 
	 * @param entity Nombre de la entidad a consultar.
	 * 
	 * @return el detalle de las políticas de acceso de la entidad indicada.
	 */
	public EntityAccessPolicy getPublicEntityPolicies(String entity);
}
