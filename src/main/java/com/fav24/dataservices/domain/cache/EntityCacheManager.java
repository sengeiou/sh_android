package com.fav24.dataservices.domain.cache;

import java.util.HashSet;
import java.util.Set;


public class EntityCacheManager extends CacheManagerConfiguration
{
	private String name;
	private CacheManagerConfiguration defaultCacheManagerConfiguration;
	private Set<EntityCache> entitiesCacheConfigurations;

	/**
	 * Gestor de caché para entidades.
	 * 
	 * @param defaultCacheManagerConfiguration Configuración base para este gestor.
	 */
	public EntityCacheManager(CacheManagerConfiguration defaultCacheManagerConfiguration) {

		this.defaultCacheManagerConfiguration = defaultCacheManagerConfiguration;
		this.entitiesCacheConfigurations = new HashSet<EntityCache>();
	}

	/**
	 * Retorna el nombre de este gestor de caché.
	 * 
	 * @return el nombre de este gestor de caché.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Asigna el nombre de este gestor de caché.
	 * 
	 * @param name El nuevo nombre para este gestor;
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna la configuración por defecto para este gestor de caché.
	 * 	
	 * @return la configuración por defecto para este gestor de caché.
	 */
	public CacheManagerConfiguration getDefaultCacheManagerConfiguration() {
		return defaultCacheManagerConfiguration;
	}

	/**
	 * Asigna la configuración por defecto para este gestor de caché.
	 * 
	 * @param defaultCacheManagerConfiguration La nueva configuración por defecto. 
	 */
	public void setDefaultCacheManagerConfiguration(
			CacheManagerConfiguration defaultCacheManagerConfiguration) {
		this.defaultCacheManagerConfiguration = defaultCacheManagerConfiguration;
	}

	/**
	 * Retorna el conjunto de configuraciones de caché de entidades, para este gestor.
	 * 
	 * @return el conjunto de configuraciones de caché de entidades, para este gestor.
	 */
	public Set<EntityCache> getEntitiesCacheConfigurations() {
		return entitiesCacheConfigurations;
	}

	/**
	 * Asigna el set de caché de entidades para este gestor.
	 * 
	 * @param entitiesCacheConfigurations El set de cachés de entidades a asignar.
	 */
	public void setEntitiesCacheConfigurations(
			Set<EntityCache> entitiesCacheConfigurations) {
		this.entitiesCacheConfigurations = entitiesCacheConfigurations;
	}

	/**
	 * Añade a la lista de gestores de caché de entidades, el especificado por parámetro.
	 * 
	 * @param entityCache Gestor de caché a añadir.
	 * 
	 * @return true en caso de que la configuración para la entidad no existiera préviamente.
	 */
	public boolean addEntityCacheConfiguration(EntityCache entityCache) {
		return entitiesCacheConfigurations.add(entityCache);
	}
}
