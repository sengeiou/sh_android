package com.fav24.dataservices.service.cache.impl;

import java.util.AbstractList;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.service.cache.RetrieveCacheConfigurationService;


/**
 * Implementación del servicio de consulta de las configuraciones de la caché.
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class RetrieveCacheConfigurationServiceImpl implements RetrieveCacheConfigurationService {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<EntityCacheManager> getCacheManagers() {

		if (Cache.getSystemCache() == null) {
			
			return null;
		}
		
		return Cache.getSystemCache().getEntityCacheManagers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityCacheManager getCacheManagerConfiguration(String cacheManager) {
		
		if (Cache.getSystemCache() == null) {
			
			return null;
		}
		
		return Cache.getSystemCache().getEntityCacheManager(cacheManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<EntityCache> getCacheManagerCaches(String cacheManager) {
		
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
	public EntityCache getCacheConfiguration(String cacheManager, String entity) {
		
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
