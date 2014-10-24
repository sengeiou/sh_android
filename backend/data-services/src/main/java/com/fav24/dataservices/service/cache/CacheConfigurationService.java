package com.fav24.dataservices.service.cache;

import java.io.InputStream;
import java.util.AbstractList;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio de carga y consulta de la configuración de la caché. 
 */
public interface CacheConfigurationService extends CacheService {

	public static final Logger logger = LoggerFactory.getLogger(CacheConfigurationService.class);

	public static final String CACHE_FILES_RELATIVE_LOCATION = "cache";
	public static final String CACHE_FILES_SUFFIX = ".cache.xml";

	/**
	 * Elimina la configuración actual de la caché, y vacía todas las cachés.
	 */
	public void dropSystemCache();

	/**
	 * Inicializa la configuración de caché de una entidad.
	 * 
	 * @param cacheManager Gestor de caché que contiene la caché de la entidad indicada.
	 * @param entity Entidad de la que se mostrará la configuración de caché.
	 * 
	 * @throws ServerException 
	 */
	public void resetCache(String cacheManager, String entity) throws ServerException;

	/**
	 * Carga las configuraciones de caché por defecto. 
	 * 
	 * @throws ServerException
	 */
	public void loadDefaultCacheConfiguration() throws ServerException;

	/**
	 * Retorna la configuración de caché cargada. 
	 * 
	 * @param cacheConfigurationStream Configuración de caché a cargar.
	 *  
	 * @return la configuración de caché cargada.
	 * 
	 * @throws ServerException 
	 */
	public Cache loadCacheConfiguration(InputStream cacheConfigurationStream) throws ServerException;

	/**
	 * Retorna la lista de gestores de caché de entidades configurados.
	 * 
	 * @return la lista de gestores de caché de entidades configurados.
	 */
	public AbstractList<EntityCacheManager> getCacheManagers();

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
	public Set<EntityCache> getCacheManagerCaches(String cacheManager);

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
