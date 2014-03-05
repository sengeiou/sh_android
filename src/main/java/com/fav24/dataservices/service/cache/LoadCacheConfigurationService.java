package com.fav24.dataservices.service.cache;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.exception.ServerException;


/**
 * Interfaz de servicio de carga de la configuración de la caché. 
 * 
 * @author Fav24
 */
public interface LoadCacheConfigurationService extends CacheService {

	public static final Logger logger = LoggerFactory.getLogger(LoadCacheConfigurationService.class);


	/**
	 * Elimina la configuración actual de la caché, y vacía todas las cachés.
	 */
	public void dropSystemCache();

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
}
