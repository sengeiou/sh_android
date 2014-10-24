package com.fav24.dataservices.controller.rest.cache;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.domain.cache.EntityCache;
import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.dto.cache.EntityCacheDto;
import com.fav24.dataservices.dto.cache.EntityCacheManagerDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.mapper.Mapper;
import com.fav24.dataservices.service.cache.CacheConfigurationService;

/**
 * Controla las peticiones de entrada a los servicios de gestión de las cachés.
 */
@Scope("singleton")
@Controller
@RequestMapping("/cache")
public class CacheController extends BaseRestController {

	private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

	private static final String MESSAGE_CACHE_RETRIEVED_OK = "La información de la caché, se retornó correctamente.";

	@Autowired
	protected CacheConfigurationService cacheConfigurationService;

	/**
	 * Procesa una petición de información de la caché de una entidad.
	 * 
	 * @param entityCache Las entidades de las que se desea obtener la caché, o las entidades a las que se tiene acceso.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/retrieve/cache", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	EntityCacheDto getEntityCache(@RequestBody final EntityCacheDto cache) {

		EntityCacheDto result = null;

		try {

			EntityCacheManager entityCacheManager = cacheConfigurationService.getCacheManagerConfiguration(cache.getManagerName());

			if (entityCacheManager != null) {

				EntityCache entityCache = entityCacheManager.getEntityCache(cache.getEntityAlias());

				if (entityCache != null) {

					result = (EntityCacheDto)Mapper.Map(entityCache);
					result.setManagerName(cache.getManagerName());
					
					result.setAlias(cache.getAlias());
					result.setRequestor(cache.getRequestor());
					
					result.setStatusCode(BaseRestController.OK);
					result.setStatusMessage(MESSAGE_CACHE_RETRIEVED_OK);
				}
				else {
					throw new ServerException(CacheConfigurationService.ERROR_CACHE_NOT_FOUND, String.format(CacheConfigurationService.ERROR_CACHE_NOT_FOUND_MESSAGE, cache.getEntityAlias(), cache.getManagerName()));
				}
			}
			else {
				throw new ServerException(CacheConfigurationService.ERROR_CACHE_MANAGER_NOT_FOUND, String.format(CacheConfigurationService.ERROR_CACHE_MANAGER_NOT_FOUND_MESSAGE, cache.getManagerName()));
			}

		} catch (ServerException e) {

			result = new EntityCacheDto(e);
			
			result.setAlias(cache.getAlias());
			result.setRequestor(cache.getRequestor());

			e.log(logger, false);
		}

		result.getRequestor().setSystemTime(System.currentTimeMillis());

		return result;
	}

	/**
	 * Procesa una petición de información de un cierto gestor de caché.
	 * 
	 * @param cacheManager El gestor de caché a consultar.
	 * 
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/retrieve/cacheManager", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody
	EntityCacheManagerDto getEntityCacheManager(@RequestBody final EntityCacheManagerDto cacheManager) {

		EntityCacheManagerDto result = null;

		try {

			EntityCacheManager entityCacheManager = cacheConfigurationService.getCacheManagerConfiguration(cacheManager.getName());

			if (entityCacheManager != null) {

				result = (EntityCacheManagerDto)Mapper.Map(entityCacheManager);
				
				result.setAlias(cacheManager.getAlias());
				result.setRequestor(cacheManager.getRequestor());
				
				result.setStatusCode(BaseRestController.OK);
				result.setStatusMessage(MESSAGE_CACHE_RETRIEVED_OK);
			}
			else {
				throw new ServerException(CacheConfigurationService.ERROR_CACHE_MANAGER_NOT_FOUND, String.format(CacheConfigurationService.ERROR_CACHE_MANAGER_NOT_FOUND_MESSAGE, cacheManager.getName()));
			}

		} catch (ServerException e) {

			result = new EntityCacheManagerDto(e);
			
			result.setAlias(cacheManager.getAlias());
			result.setRequestor(cacheManager.getRequestor());

			e.log(logger, false);
		}

		result.getRequestor().setSystemTime(System.currentTimeMillis());

		return result;
	}
}
