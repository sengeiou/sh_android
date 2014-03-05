package com.fav24.dataservices.service.security;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.AccessPolicyFiles;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio de carga de las políticas de acceso. 
 * 
 * @author Fav24
 */
public interface LoadAccessPolicyService extends AccessPolicyService {

	public static final Logger logger = LoggerFactory.getLogger(LoadAccessPolicyService.class);


	/**
	 * Elimina todas las políticas de acceso disponibles hasta el momento.
	 * 
	 * @throws ServerException
	 */
	public void dropAccessPolicies() throws ServerException;

	/**
	 * Carga las políticas de acceso por defecto. 
	 * 
	 * @throws ServerException 
	 */
	public void loadDefaultAccessPolicy() throws ServerException;

	/**
	 * Retorna los ficheros cargados. 
	 * 
	 * @param accessPolicyFiles Políticas de acceso a cargar.
	 *  
	 * @return los ficheros cargados.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicyFiles loadAccessPolicy(AccessPolicyFiles accessPolicyFiles) throws ServerException;

	/**
	 * Retorna las políticas cargadas. 
	 * 
	 * @param accessPolicyStream Políticas de acceso a cargar.
	 *  
	 * @return las políticas cargadas.
	 * 
	 * @throws ServerException 
	 */
	public AccessPolicy loadAccessPolicy(InputStream accessPolicyStream) throws ServerException;
}
