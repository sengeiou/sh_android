package com.fav24.dataservices.domain.cache;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Cache
{
	public static final String APPLICATION_CACHE_FILES_SUFFIX = ".cache.xml";

	private static Cache systemCache;

	private String version;
	private String description;
	private CacheManagerConfiguration defaultCacheManagerConfiguration;
	private AbstractList<EntityCacheManager> entityCacheManagers;
	private Map<String, EntityCacheManager> cacheCacheManagers;

	/**
	 * Constructor por defecto.
	 */
	public Cache() { 

		this.version = null;
		this.description = null;
		this.defaultCacheManagerConfiguration = null;
		this.entityCacheManagers = new ArrayList<EntityCacheManager>();
		this.cacheCacheManagers = new HashMap<String, EntityCacheManager>(); 
	}

	/**
	 * Constructor con parámetros.
	 */
	public Cache(String version, String description) {

		this.version = version;
		this.description = description;
		this.defaultCacheManagerConfiguration = null;
		this.entityCacheManagers = new ArrayList<EntityCacheManager>();
		this.cacheCacheManagers = new HashMap<String, EntityCacheManager>(); 
	}

	/**
	 * Retorna la versión de data-services para la que fué creado esta configuración de caché.
	 *  
	 * @return la versión de data-services para la que fué creado esta configuración de caché.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Asigna la versión de data-services para la que fué creado esta configuración de caché.
	 *  
	 * @param version La versión de data-services para la que fué creado esta configuración de caché.
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Retorna una descripción de esta configuración de caché.
	 * 
	 * @return una descripción de esta configuración de caché.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Asigna una descripción de esta configuración de caché.
	 * 
	 * @param description Descripción a asignar a esta configuración de caché.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Retorna la configuración global por defecto para configuración de gestores de caché.
	 * 	
	 * @return la configuración global por defecto para configuración de gestores de caché.
	 */
	public CacheManagerConfiguration getDefaultCacheManagerConfiguration() {
		return defaultCacheManagerConfiguration;
	}

	/**
	 * Asigna la configuración global por defecto para configuración de gestores de caché.
	 * 
	 * @param defaultCacheManagerConfiguration La nueva configuración global por defecto. 
	 */
	public void setDefaultCacheManagerConfiguration(
			CacheManagerConfiguration defaultCacheManagerConfiguration) {
		this.defaultCacheManagerConfiguration = defaultCacheManagerConfiguration;
	}

	/**
	 * Retorna la lista de gestores de caché de entidades.
	 * 
	 * @return la lista de gestores de caché de entidades.
	 */
	public AbstractList<EntityCacheManager> getEntityCacheManagers() {
		return entityCacheManagers;
	}

	/**
	 * Añade a la lista de gestores de caché de entidades, el especificado por parámetro.
	 * 
	 * @param entityCacheManager Gestor de caché a añadir.
	 * 
	 * @return true (as specified by Collection.add)
	 */
	public boolean addEntityCacheManager(EntityCacheManager entityCacheManager) {

		boolean result = entityCacheManagers.add(entityCacheManager);

		for (EntityCache entityCache : entityCacheManager.getEntitiesCacheConfigurations()) {
			cacheCacheManagers.put(entityCache.getAlias(), entityCacheManager);
		}

		return result;
	}

	/**
	 * Retorna el gestor de caché solicitado por nombre.
	 * 
	 * @param cacheManager Alias de la caché de la que se desea obtener el gestor.
	 * 
	 * @return el gestor de caché solicitado por nombre.
	 */
	public EntityCacheManager getEntityCacheManager(String cacheManager) {

		if (cacheManager == null) {

			return null;
		}

		for (EntityCacheManager entityCacheManager : entityCacheManagers) {

			if (cacheManager.equals(entityCacheManager.getName())) {

				return entityCacheManager;
			}
		}

		return null;
	}

	/**
	 * Retorna el gestor de caché que contiene la caché de alias el indicado.
	 * 
	 * @param cacheAlias Alias de la caché de la que se desea obtener el gestor.
	 * 
	 * @return el gestor de caché que contiene la caché de alias el indicado.
	 */
	public EntityCacheManager getCacheManager(String cacheAlias) {
		return cacheCacheManagers.get(cacheAlias);
	}

	/**
	 * Retorna la instancia de la caché para la entidad del alias indicado.
	 * 
	 * @param alias Alias de la entidad de la que se desea obtener la instancia de caché.
	 *  
	 * @return la instancia de la caché para la entidad del alias indicado.
	 */
	public net.sf.ehcache.Cache getCache(String cacheAlias) {

		EntityCacheManager entityCacheManager = cacheCacheManagers.get(cacheAlias);

		if (entityCacheManager != null) {
			return entityCacheManager.getCache(cacheAlias);
		}

		return null;
	}

	/**
	 * Modifica las políticas de acceso efectivas a partir de este momento.
	 * 
	 * En el caso de coincidir entidades, sustituye las existentes por las indicadas por parámetro.
	 * 
	 * @param cacheConfiguration Configuración a añadir/sustituir.
	 */
	public static final void mergeCurrentCacheConfiguration(final Cache cacheConfiguration) {

		synchronized(Cache.class) {

			if (cacheConfiguration != null) {

				if (systemCache == null) {
					systemCache = cacheConfiguration.clone();

					for (EntityCacheManager entityCacheManager : systemCache.getEntityCacheManagers()) {

						entityCacheManager.constructCacheManager();
					}
				}
				else {
					systemCache.mergeCacheConfiguration(cacheConfiguration);
				}
			}
			
			Collections.sort(systemCache.entityCacheManagers);
		}
	}

	/**
	 * Modifica la configuración de la caché, sustituyendo las existentes coincidentes por las indicadas por parámetro.
	 * 
	 * Sustituye las cachés actuales coincidentes, por las nuevas.
	 * Esto implica que se rompen las cachés coincidente existentes.
	 * 
	 * @param cacheConfiguration Configuración a añadir/sustituir.
	 */
	public void mergeCacheConfiguration(final Cache cacheConfiguration) {

		if (cacheConfiguration != null) {

			/*
			 * Se crea la nueva lista de gestores de caché, con sus configuraciones de caché.
			 */
			AbstractList<EntityCacheManager> newEntityCacheManagers = new ArrayList<EntityCacheManager>(cacheConfiguration.entityCacheManagers.size());

			for (EntityCacheManager entityCacheManager : cacheConfiguration.entityCacheManagers) {

				newEntityCacheManagers.add(entityCacheManager.clone());
			}

			/*
			 * Se eliminan las cachés existentes que serán sustituidas por las nuevas.
			 */
			for (EntityCacheManager newEntityCacheManager : newEntityCacheManagers) {

				for (EntityCache newEntityCache : newEntityCacheManager.getEntitiesCacheConfigurations()) {

					for (EntityCacheManager entityCacheManager : entityCacheManagers) {

						if (entityCacheManager.removeEntityCache(newEntityCache.getAlias())) {
							break;
						}
					}
				}
			}

			/*
			 * Se mueven las configuraciones de las cachés existentes a los nuevos gestores.
			 */
			for (EntityCacheManager newEntityCacheManager : newEntityCacheManagers) {

				Iterator<EntityCacheManager> entityCacheManagerIterator = entityCacheManagers.iterator();
				while (entityCacheManagerIterator.hasNext()) {

					EntityCacheManager entityCacheManager = entityCacheManagerIterator.next();

					if (entityCacheManager.getName() == newEntityCacheManager.getName() || 
							(entityCacheManager.getName() != null && entityCacheManager.getName().equals(newEntityCacheManager.getName()))
							) {

						for (EntityCache entityCache : entityCacheManager.getEntitiesCacheConfigurations()) {

							if (!newEntityCacheManager.containsEntityCacheConfiguration(entityCache.getAlias())) {
								newEntityCacheManager.addEntityCacheConfiguration(entityCache.clone());
							}
						}

						entityCacheManager.destroy();
						entityCacheManagerIterator.remove();
					}
				}
			}

			/*
			 * Se construyen y añaden las nuevas conifguraciones de caché.
			 */
			for (EntityCacheManager newEntityCacheManager : newEntityCacheManagers) {

				newEntityCacheManager.constructCacheManager();

				addEntityCacheManager(newEntityCacheManager);
			}
		}
	}

	/**
	 * Rompe todas las cachés activas.
	 * Elimina todas las configuraciones de caché establecidas hasta el momento.
	 */
	public static final void destroy() {

		synchronized(Cache.class) {

			if (systemCache != null) {

				for (EntityCacheManager entityCacheManager : systemCache.entityCacheManagers) {

					entityCacheManager.destroy();
				}
				systemCache = null;
			}
		}
	}

	/**
	 * Retorna la referencia a la caché actual del sistema.
	 * 
	 * Esta estructura, contiene tanto la información de configuración, como la de actividad.
	 * 
	 * @return la referencia a la caché actual del sistema.
	 */
	public static final Cache getSystemCache() {
		return systemCache;
	}
	
	/**
	 * Asigna la referencia a la nueva caché del sistema.
	 * 
	 * @param systemCache La referencia a la nueva caché del sistema a asignar.
	 */
	public static final void setSystemCache(Cache systemCache) {
		
		destroy();
		
		Cache.systemCache = systemCache;
	}

	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @return la nueva instancia.
	 */
	public Cache clone() {

		Cache clone = new Cache();

		clone.version = version;
		clone.description = description;

		if (defaultCacheManagerConfiguration == null) {
			clone.defaultCacheManagerConfiguration = null;
		}
		else {
			clone.defaultCacheManagerConfiguration = defaultCacheManagerConfiguration.clone(null);
		}

		if (entityCacheManagers == null) {
			clone.entityCacheManagers = null;
		}
		else {
			clone.entityCacheManagers = new ArrayList<EntityCacheManager>(entityCacheManagers.size());

			for(EntityCacheManager entityCacheManager : entityCacheManagers) {

				clone.entityCacheManagers.add(entityCacheManager.clone());
			}
		}

		return clone;
	}
}
