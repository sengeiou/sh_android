package com.fav24.dataservices.domain.cache;

import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;



/**
 * Configuración de un gestor de cachés. 
 */
public class CacheManagerConfiguration
{
	public static final Long DEFAULT_MAX_BYTES_LOCAL_HEAP = null;
	public static final Long DEFAULT_MAX_BYTES_LOCAL_DISK = null;
	public static final DiskStore DEFAULT_DISK_STORE = new DiskStore();
	public static final CacheConfiguration DEFAULT_CACHE_CONFIGURATION = null;

	private CacheManagerConfiguration parentConfiguration;

	private Long maxBytesLocalHeap;
	private Long maxBytesLocalDisk;
	private DiskStore diskStore;
	private CacheConfiguration defaultCacheConfiguration;


	/**
	 * Constructor por defecto.
	 */
	public CacheManagerConfiguration() {
	}

	/**
	 * Constructor con parámetros.
	 * 
	 * @param parentConfiguration Configuración padre para los valores por defecto.
	 */
	public CacheManagerConfiguration(CacheManagerConfiguration parentConfiguration) {

		this.parentConfiguration = parentConfiguration;
	}

	/**
	 * Retorna la configuración por defecto establecida por su antecesor.
	 * 
	 * @return la configuración por defecto establecida por su antecesor.
	 */
	public CacheManagerConfiguration getParentConfiguration() {
		return parentConfiguration;
	}

	/**
	 * Asigna la configuración por defecto establecida por su antecesor.
	 * 
	 * @param parentConfiguration La configuración por defecto a asignar.
	 */
	public void setParentConfiguration(CacheManagerConfiguration parentConfiguration) {
		this.parentConfiguration = parentConfiguration;
	}
	/**
	 * Constructor con parámetros.
	 * 
	 * @param defaultCacheConfiguration Configuración base para las cachés definidas por defecto.
	 */
	public CacheManagerConfiguration(CacheConfiguration defaultCacheConfiguration) {

		this.defaultCacheConfiguration = defaultCacheConfiguration;
	}

	/**
	 * Retorna el número máximo de bytes en memoria.
	 * 
	 * @return el número máximo de bytes en memoria.
	 */
	public Long getMaxBytesLocalHeap() {

		if (maxBytesLocalHeap != null) {
			return maxBytesLocalHeap;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getMaxBytesLocalHeap();
		}

		return DEFAULT_MAX_BYTES_LOCAL_HEAP; 
	}

	/**
	 * Asigna el número máximo de bytes en memoria.
	 * 
	 * @param maxBytesLocalHeap Nuevo límite de bytes en memoria.
	 */
	public void setMaxBytesLocalHeap(Long maxBytesLocalHeap) {
		this.maxBytesLocalHeap = maxBytesLocalHeap;
	}

	/**
	 * Retorna el número máximo de bytes en disco.
	 * 
	 * @return el número máximo de bytes en disco.
	 */
	public Long getMaxBytesLocalDisk() {

		if (maxBytesLocalDisk != null) {
			return maxBytesLocalDisk;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getMaxBytesLocalDisk();
		}

		return DEFAULT_MAX_BYTES_LOCAL_DISK; 
	}

	/**
	 * Asigna el número máximo de bytes en disco.
	 * 
	 * @param maxBytesLocalHeap Nuevo límite de bytes en disco.
	 */
	public void setMaxBytesLocalDisk(Long maxBytesLocalDisk) {
		this.maxBytesLocalDisk = maxBytesLocalDisk;
	}

	/**
	 * Retorna la ubicación de los ficheros de persistencia de la caché.
	 * 
	 * @return la ubicación de los ficheros de persistencia de la caché.
	 */
	public DiskStore getDiskStore() {

		if (diskStore != null) {
			return diskStore;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getDiskStore();
		}

		return DEFAULT_DISK_STORE; 
	}

	/**
	 * Asigna la ubicación de los ficheros de persistencia de la caché.
	 * 
	 * @param diskStore La nueva ubicación a asignar.
	 */
	public void setDiskStore(DiskStore diskStore) {
		this.diskStore = diskStore;
	}

	/**
	 * Retorna la configuración por defecto para las cachés de etidades asignadas a 
	 * este gestor de caché.
	 * 	
	 * @return la configuración por defecto para las cachés de etidades asignadas a 
	 * este gestor de caché.
	 */
	public CacheConfiguration getDefaultCacheConfiguration() {

		if (defaultCacheConfiguration != null) {
			return defaultCacheConfiguration;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getDefaultCacheConfiguration();
		}

		return DEFAULT_CACHE_CONFIGURATION; 
	}

	/**
	 * Asigna la configuración por defecto para las cachés de etidades asignadas a 
	 * este gestor de caché.
	 * 
	 * @param defaultCacheConfiguration La nueva configuración por defecto. 
	 */
	public void setDefaultCacheConfiguration(CacheConfiguration defaultCacheConfiguration) {
		this.defaultCacheConfiguration = defaultCacheConfiguration;
	}

	/**
	 * Retorna la configuración del gestor de caché construida.
	 * 
	 * @param name Nombre del gestor que usará esta configuración. 
	 * 
	 * @return la configuración del gestor de caché construida.
	 */
	public Configuration constructCacheManagerConfiguration(String name) {

		Configuration configuration;

		configuration = new Configuration();
		configuration.setDynamicConfig(true);
		configuration.setName(name);

		configuration.setMaxBytesLocalHeap(getMaxBytesLocalHeap());
		configuration.setMaxBytesLocalDisk(getMaxBytesLocalDisk());

		DiskStoreConfiguration diskStoreConfiguration = new DiskStoreConfiguration();
		diskStoreConfiguration.setPath(getDiskStore().getPath());

		configuration.addDiskStore(diskStoreConfiguration);

		if (getDefaultCacheConfiguration() != null) {
			configuration.setDefaultCacheConfiguration(getDefaultCacheConfiguration().constructCacheConfiguration(null));
		}
		else {
			configuration.setDefaultCacheConfiguration(CacheConfiguration.DefaultCacheConfiguration());
		}

		return configuration;
	}

	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @param clone Nueva instancia a poblar, o <code>null</code>.
	 * 
	 * @return la nueva instancia poblada.
	 */
	public CacheManagerConfiguration clone(CacheManagerConfiguration clone) {

		if (clone == null) {
			clone = new CacheManagerConfiguration();
		}

		if (parentConfiguration == null) {
			clone.parentConfiguration = null;
		}
		else {
			clone.parentConfiguration = parentConfiguration.clone(null);
		}

		clone.maxBytesLocalHeap = maxBytesLocalHeap;
		clone.maxBytesLocalDisk = maxBytesLocalDisk;

		if (diskStore == null) {
			clone.diskStore = null;
		}
		else {
			clone.diskStore = diskStore.clone();
		}

		if (defaultCacheConfiguration == null) {
			clone.defaultCacheConfiguration = null;
		}
		else {
			clone.defaultCacheConfiguration = defaultCacheConfiguration.clone(null);
		}

		return clone;
	}
}
