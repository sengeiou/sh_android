package com.fav24.dataservices.domain.cache;

import java.util.AbstractList;
import java.util.ArrayList;


public class Cache
{
	private String version;
	private String description;
	private CacheManagerConfiguration defaultCacheManagerConfiguration;
	private AbstractList<EntityCacheManager> entityCacheManagers;

	/**
	 * Constructor por defecto.
	 */
	public Cache() { 
		entityCacheManagers = new ArrayList<EntityCacheManager>();
	}

	/**
	 * Constructor con parámetros.
	 */
	public Cache(String version, String description) {

		this.entityCacheManagers = new ArrayList<EntityCacheManager>();

		this.version = version;
		this.description = description;
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
	 * Asigna la lista de gestores de caché de entidades.
	 * 
	 * @param entityCacheManagers La nueva lista de gestores de caché de entidades a asignar.
	 */
	public void setEntityCacheManagers(AbstractList<EntityCacheManager> entityCacheManagers) {
		this.entityCacheManagers = entityCacheManagers;
	}

	/**
	 * Añade a la lista de gestores de caché de entidades, el especificado por parámetro.
	 * 
	 * @param entityCacheManager Gestor de caché a añadir.
	 * 
	 * @return true (as specified by Collection.add)
	 */
	public boolean addEntityCacheManager(EntityCacheManager entityCacheManager) {
		return entityCacheManagers.add(entityCacheManager);
	}
}
