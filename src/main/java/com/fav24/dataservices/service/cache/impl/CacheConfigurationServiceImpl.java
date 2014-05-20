package com.fav24.dataservices.service.cache.impl;

import java.io.InputStream;
import java.util.AbstractList;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.cache.CacheConfigurationService;
import com.fav24.dataservices.xml.cache.CacheDOM;


/**
 * Implementación del servicio de carga y consulta de la configuración de la caché.
 */
@Scope("singleton")
@Component
public class CacheConfigurationServiceImpl implements CacheConfigurationService {


	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void dropSystemCache() {
		Cache.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void loadDefaultCacheConfiguration() throws ServerException {

		Cache.destroy();
		Cache.loadDefaultCacheConfigurations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized Cache loadCacheConfiguration(InputStream cacheConfigurationStream) throws ServerException {

		Cache cache = new CacheDOM(cacheConfigurationStream);

		Cache.mergeCurrentCacheConfiguration(cache);

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
