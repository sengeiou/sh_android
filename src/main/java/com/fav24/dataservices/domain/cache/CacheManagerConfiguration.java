package com.fav24.dataservices.domain.cache;



/**
 * Configuración de un gestor de cachés. 
 */
public class CacheManagerConfiguration
{
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
	public void getParentConfiguration(CacheManagerConfiguration parentConfiguration) {
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
		return maxBytesLocalHeap;
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
		return maxBytesLocalDisk;
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
		return diskStore;
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
		return defaultCacheConfiguration;
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
}
