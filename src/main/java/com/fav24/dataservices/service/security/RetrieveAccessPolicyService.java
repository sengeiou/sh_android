package com.fav24.dataservices.service.security;

import java.util.AbstractList;

import com.fav24.dataservices.dto.security.AccessPolicyResultDto;
import com.fav24.dataservices.dto.security.AccessPolicyDto;
import com.fav24.dataservices.security.EntityAccessPolicy;


/**
 * Interfaz de servicio de consulta de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface RetrieveAccessPolicyService {

	/**
	 * Retorna el detalle de las políticas de acceso de las entidades. 
	 * 
	 * @param accessPolicy Políticas de acceso solicitadas.
	 *  
	 * @return el detalle de las políticas de acceso de las entidades indicadas.
	 */
	public AccessPolicyResultDto getCurrentAccessPolicy(AccessPolicyDto accessPolicy);
	
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
	public EntityAccessPolicy getPublicEntityPolicy(String entity);
}
