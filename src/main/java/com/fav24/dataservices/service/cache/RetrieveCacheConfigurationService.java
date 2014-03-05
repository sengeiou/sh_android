package com.fav24.dataservices.service.cache;

import java.util.AbstractList;

import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;


/**
 * Interfaz de servicio de consulta de las configuraciones de la caché. 
 * 
 * @author Fav24
 */
public interface RetrieveCacheConfigurationService extends CacheService {

	/**
	 * Retorna la lista de gestores de caché de entidades configurados.
	 * 
	 * @return la lista de gestores de caché de entidades configurados.
	 */
	public AbstractList<String> getCacheManagers();
	
	/**
	 * Retorna el detalle de la configuración del gestor de caché de entidades indicado.
	 * 
	 * @param cacheManager Nombre del gestor de caché a consultar.
	 * 
	 * @return el detalle de la configuración del gestor de caché de entidades indicado.
	 */
	public EntityCacheManager getCacheManagerConfiguration(String cacheManager);

	/**
	 * Retorna la lista de cachés pertenecientes al gestor de caché de entidades indicado.
	 * 
	 * @param cacheManager Nombre del gestor de caché de entidades a consultar.
	 * 
	 * @return la lista de cachés pertenecientes al gestor de caché de entidades indicado.
	 */
	public AbstractList<String> getCacheManagerCaches(String cacheManager);

	/**
	 * Retorna el detalle de la configuración de la caché de la entidad indicada.
	 * 
	 * @param cacheManager Nombre del gestor de caché de entidades a consultar.
	 * @param entity Nombre de la entidad de la que se desea obtener su configuración de caché.
	 * 
	 * @return el detalle de la configuración de la caché de la entidad indicada.
	 */
	public EntityCache getCacheConfiguration(String cacheManager, String entity);
}
