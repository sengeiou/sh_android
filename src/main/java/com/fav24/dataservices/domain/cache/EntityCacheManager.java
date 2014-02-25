package com.fav24.dataservices.domain.cache;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.config.Configuration;


public class EntityCacheManager extends CacheManagerConfiguration
{
	private String name;
	private Set<EntityCache> entitiesCacheConfigurations;

	private CacheManager cacheManager;


	/**
	 * Gestor de caché para entidades.
	 */
	public EntityCacheManager() {

		this.name = null;
		this.entitiesCacheConfigurations = new HashSet<EntityCache>();
		
		this.cacheManager = null;
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
	 * Retorna el conjunto de configuraciones de caché de entidades, para este gestor.
	 * 
	 * @return el conjunto de configuraciones de caché de entidades, para este gestor.
	 */
	public synchronized Set<EntityCache> getEntitiesCacheConfigurations() {
		return entitiesCacheConfigurations;
	}

	/**
	 * Asigna el set de caché de entidades para este gestor.
	 * 
	 * @param entitiesCacheConfigurations El set de cachés de entidades a asignar.
	 */
	public synchronized void setEntitiesCacheConfigurations(
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
	public synchronized boolean addEntityCacheConfiguration(EntityCache entityCache) {
		return entitiesCacheConfigurations.add(entityCache);
	}

	/**
	 * Retorna la caché para la entidad indicada por parámetro.
	 * 
	 * @param alias Alias de la entidad de la que retornar la caché.
	 * 
	 * @return la caché para la entidad indicada por parámetro.
	 */
	public synchronized EntityCache getEntityCache(String alias) {
		
		if (entitiesCacheConfigurations == null || alias == null) {
			return null;
		}
		
		for (EntityCache currentEntityCache : entitiesCacheConfigurations) {
			
			if (alias.equals(currentEntityCache.getAlias())) {
				return currentEntityCache; 
			}
		}
		
		return null;
	}
	
	/**
	 * Retorna true o false si encontró la caché a eliminar, para la entidad indicada por parámetro.
	 * 
	 * @param alias Alias de la entidad de la que eliminar la caché.
	 * 
	 * @return true o false si encontró la caché a eliminar, para la entidad indicada por parámetro.
	 */
	public synchronized boolean removeEntityCache(String alias) {
		
		if (entitiesCacheConfigurations == null || alias == null) {
			return false;
		}
		
		Iterator<EntityCache> entityCacheConfigurationIterator = entitiesCacheConfigurations.iterator();
		
		while(entityCacheConfigurationIterator.hasNext()) {

			EntityCache currentEntityCache = entityCacheConfigurationIterator.next();
			
			if (alias.equals(currentEntityCache.getAlias())) {
				entityCacheConfigurationIterator.remove();
				
				if (cacheManager != null) {
					cacheManager.removeCache(currentEntityCache.getAlias());
				}
				
				return true; 
			}
		}
		
		return false;
	}
	
	/**
	 * Construye el gestor de caché con las cachés incluidas.
	 */
	public synchronized void constructCacheManager() {

		constructCacheManager(constructCacheManagerConfiguration());

		for (EntityCache entityCacheConfiguration : entitiesCacheConfigurations) {
			
			net.sf.ehcache.Cache entityCache = new net.sf.ehcache.Cache(entityCacheConfiguration.constructCacheConfiguration(entityCacheConfiguration.getAlias()));
			
			cacheManager.addCache(entityCache);
		}
	}
	
	/**
	 * Retorna el gestor de caché construido. 
	 * 
	 * @param configuration La estructura de configuración del gestor de caché.
	 * 
	 * @return el gestor de caché construido.
	 */
	public synchronized CacheManager constructCacheManager(Configuration configuration) {
		
		cacheManager = CacheManager.create(configuration);
		cacheManager.setName(getName());
		
		return cacheManager;
	}

	/**
	 * Rompe todas las cachés activas en este gestor.
	 * Elimina todas las configuraciones de caché establecidas hasta el momento en este gestor.
	 */
	public synchronized void destroy() {

		if (entitiesCacheConfigurations != null) {

			for (EntityCache entityCacheConfiguration : entitiesCacheConfigurations) {

				cacheManager.removeCache(entityCacheConfiguration.getAlias());
			}

			entitiesCacheConfigurations.clear();
			entitiesCacheConfigurations = null;

			cacheManager.shutdown();
			cacheManager = null;
		}
	}

	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @return la nueva instancia.
	 */
	public EntityCacheManager clone() {

		EntityCacheManager clone = new EntityCacheManager();
		
		super.clone(clone);

		clone.name = name;
		
		if (entitiesCacheConfigurations == null) {
			
			clone.entitiesCacheConfigurations = null;
		}
		else {
			
			clone.entitiesCacheConfigurations = new HashSet<EntityCache>(entitiesCacheConfigurations.size());
			
			for(EntityCache entityCache : entitiesCacheConfigurations) {
				
				clone.entitiesCacheConfigurations.add(entityCache.clone());	
			}
		}
		
		clone.cacheManager = null;
		
		return clone;
	}
}
