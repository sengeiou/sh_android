package com.fav24.dataservices.service.cache.impl;

import java.io.InputStream;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fav24.dataservices.domain.cache.Cache;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.service.cache.LoadCacheConfigurationService;
import com.fav24.dataservices.xml.cache.CacheDOM;


/**
 * Implementación del servicio de carga de la configuración de la caché.
 * 
 * @author Fav24
 */
@Component
@Scope("prototype")
public class LoadCacheConfigurationServiceImpl implements LoadCacheConfigurationService {


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dropSystemCache() {
		Cache.destroy();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadDefaultCacheConfiguration() throws ServerException {

		Cache.destroy();
		Cache.loadDefaultCacheConfigurations();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cache loadCacheConfiguration(InputStream cacheConfigurationStream) throws ServerException {

		Cache cache = new CacheDOM(cacheConfigurationStream);

		Cache.mergeCurrentCacheConfiguration(cache);

		return cache;
	}
}
