package com.fav24.dataservices.service.cache.impl;

import java.util.AbstractList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
	public AbstractList<String> getCacheManagers() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityCacheManager getCacheManagerConfiguration(String cacheManager) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AbstractList<String> getCacheManagerCaches(String cacheManager) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityCache getCacheConfiguration(String cacheManager, String entity) {
		// TODO Auto-generated method stub
		return null;
	}
}
