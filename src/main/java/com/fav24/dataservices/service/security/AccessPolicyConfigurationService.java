package com.fav24.dataservices.service.security;

import java.io.InputStream;
import java.util.AbstractList;
import java.util.NavigableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.security.AccessPolicy;
import com.fav24.dataservices.domain.security.RemoteFiles;
import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.generic.hook.GenericServiceHook;


/**
 * Interfaz de servicio de carga y consulta de las políticas de acceso. 
 */
public interface AccessPolicyConfigurationService extends AccessPolicyService {

	public static final Logger logger = LoggerFactory.getLogger(AccessPolicyConfigurationService.class);


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
	public RemoteFiles loadAccessPolicy(RemoteFiles accessPolicyFiles) throws ServerException;

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

	/**
	 * Carga los hooks que serán asignables desde las políticas de acceso.
	 */
	public void loadDefaultHooks() throws ServerException;

	/**
	 * Descarga el conjunto de hooks.
	 */
	public void dropHooks() throws ServerException;

	/**
	 * Retorna el conjunto de hooksdisponible.
	 * 
	 * @return el conjunto de hooksdisponible.
	 */
	public NavigableMap<String, GenericServiceHook> getAvailableHooks();

	/**
	 * Retorna el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 * 
	 * @param alias Alias del hook a localizar.
	 * 
	 * @return el hook solicidato, o <code>null</code> en caso de no estar disponible.
	 */
	public GenericServiceHook getHook(String alias);

	/**
	 * Carga el conjunto de hooks especificados en la estructura suministrada.
	 * 
	 * @param hookFiles Conjunto de hooks a cargar.
	 * 
	 * @return los archivos de hooks cargados.
	 * 
	 * @throws ServerException
	 */
	public RemoteFiles loadHooks(RemoteFiles hookFiles) throws ServerException;
}
