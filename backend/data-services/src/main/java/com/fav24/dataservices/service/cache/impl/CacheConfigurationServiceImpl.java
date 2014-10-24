package com.fav24.dataservices.service.cache.impl;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.DataServicesContext;
import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.cache.CacheConfigurationService;
import com.fav24.dataservices.service.system.SystemService;
import com.fav24.dataservices.util.FileUtils;
import com.fav24.dataservices.xml.cache.CacheDOM;


/**
 * Implementación del servicio de carga y consulta de la configuración de la caché.
 */
@Scope("singleton")
@Component
public class CacheConfigurationServiceImpl implements CacheConfigurationService {

	@Autowired
	protected SystemService systemService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropSystemCache() {

		systemService.updateCacheMeters(null);

		Cache.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void resetCache(String cacheManager, String cache) throws ServerException {

		EntityCacheManager entityCacheManager = getCacheManagerConfiguration(cacheManager);
		if (entityCacheManager == null) {
			throw new ServerException(ERROR_CACHE_MANAGER_NOT_FOUND, 
					String.format(ERROR_CACHE_MANAGER_NOT_FOUND_MESSAGE, cacheManager));
		}
		
		net.sf.ehcache.Cache cacheInstance = entityCacheManager.getCache(cache);
		if (cacheInstance == null) {
			throw new ServerException(ERROR_CACHE_NOT_FOUND, 
					String.format(ERROR_CACHE_NOT_FOUND_MESSAGE, cacheManager, cache));
		}
		
		cacheInstance.removeAll();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultCacheConfiguration() throws ServerException {

		String applicationHome = DataServicesContext.getCurrentDataServicesContext().getApplicationHome();

		// Se cargan los archivos de políticas de seguridad existentes.
		File applicationHomeDir = new File(applicationHome);

		if (applicationHomeDir.exists() && applicationHomeDir.isDirectory()) {

			AbstractList<File> cacheConfigurationFiles = FileUtils.getFilesWithSuffix(applicationHome, CACHE_FILES_SUFFIX, null);

			if (cacheConfigurationFiles.size() > 0) {

				AbstractList<Cache> loadedCaches = new ArrayList<Cache>();

				for(File cacheConfigurationFile : cacheConfigurationFiles) {

					try {
						Cache loadedCache = new CacheDOM(cacheConfigurationFile.toURI().toURL());

						loadedCaches.add(loadedCache);
					} 
					catch (MalformedURLException e) {
						throw new ServerException(ERROR_INVALID_CACHE_CONFIGURATION_FILE_URL, 
								String.format(ERROR_INVALID_CACHE_CONFIGURATION_FILE_URL_MESSAGE, cacheConfigurationFile.toURI().toString()));
					}
				}

				// Se aplican las configuraciones de caché cargadas.
				Cache.destroy();
				for(Cache loadedCache : loadedCaches) {
					Cache.mergeCurrentCacheConfiguration(loadedCache);
				}
				
				systemService.updateCacheMeters(Cache.getSystemCache().getEntityCacheManagers());
			}
		}
		else {

			throw new ServerException(DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED, 
					DataServicesContext.ERROR_APPLICATION_CONTEXT_APPLICATION_HOME_NOT_DEFINED_MESSAGE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Cache loadCacheConfiguration(InputStream cacheConfigurationStream) throws ServerException {

		Cache cache = new CacheDOM(cacheConfigurationStream);

		Cache.mergeCurrentCacheConfiguration(cache);

		systemService.updateCacheMeters(Cache.getSystemCache().getEntityCacheManagers());
		
		return cache;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized AbstractList<EntityCacheManager> getCacheManagers() {

		if (Cache.getSystemCache() == null) {

			return null;
		}

		return Cache.getSystemCache().getEntityCacheManagers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized EntityCacheManager getCacheManagerConfiguration(String cacheManager) {

		if (Cache.getSystemCache() == null) {

			return null;
		}

		return Cache.getSystemCache().getEntityCacheManager(cacheManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Set<EntityCache> getCacheManagerCaches(String cacheManager) {

		if (Cache.getSystemCache() == null) {

			return null;
		}

		EntityCacheManager entityCacheManager = Cache.getSystemCache().getEntityCacheManager(cacheManager);

		if (entityCacheManager != null) {
			return entityCacheManager.getEntitiesCacheConfigurations();
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized EntityCache getCacheConfiguration(String cacheManager, String entity) {

		if (Cache.getSystemCache() == null) {

			return null;
		}

		EntityCacheManager entityCacheManager = Cache.getSystemCache().getEntityCacheManager(cacheManager);

		if (entityCacheManager != null) {
			return entityCacheManager.getEntityCache(entity);
		}

		return null;
	}
}
