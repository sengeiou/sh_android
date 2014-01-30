package com.fav24.dataservices.service.security;

import java.util.AbstractList;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;
import com.fav24.dataservices.security.EntityAccessPolicy;


/**
 * Interfaz de servicio de consulta de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface RetrieveAccessPolicyService extends AccessPolicyService {

	/**
	 * Retorna el detalle de las políticas de acceso de las entidades. 
	 * 
	 * @param accessPolicy Políticas de acceso solicitadas.
	 *  
	 * @return el detalle de las políticas de acceso de las entidades indicadas.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicy getCurrentAccessPolicy(AccessPolicy accessPolicy) throws ServerException;
	
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
