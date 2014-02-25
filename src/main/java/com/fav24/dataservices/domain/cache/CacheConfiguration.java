package com.fav24.dataservices.domain.cache;

import net.sf.ehcache.config.PersistenceConfiguration;


/**
 * Configuración de una caché.
 */
public class CacheConfiguration
{
	public static final Long DEFAULT_TIME_TO_IDLE_SECONDS = 120L;
	public static final Long DEFAULT_TIME_TO_LIVE_SECONDS = 120L;
	public static final Boolean DEFAULT_ETERNAL = false;
	public static final Long DEFAULT_MAX_BYTES_LOCAL_HEAP = null;
	public static final Long DEFAULT_MAX_ENTRIES_LOCAL_HEAP = 10000L;
	public static final Long DEFAULT_MAX_BYTES_DISK = null;
	public static final Long DEFAULT_MAX_ENTRIES_DISK = 10000000L;
	public static final Long DEFAULT_DISK_EXPIRITY_THREAD_INTERVAL_SECONDS = 120L;
	public static final MemoryStoreEvictionPolicy DEFAULT_MEMORY_EVICTION_POLICY = MemoryStoreEvictionPolicy.LRU;
	public static final Persistence DEFAULT_PERSISTENCE = new Persistence(true);

	private CacheConfiguration parentConfiguration;

	private Long timeToIdleSeconds;
	private Long timeToLiveSeconds;
	private Boolean eternal;
	private Long maxBytesLocalHeap;
	private Long maxEntriesLocalHeap;
	private Long maxBytesLocalDisk;
	private Long maxEntriesLocalDisk;
	private Long diskExpiryThreadIntervalSeconds;
	private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy;
	private Persistence persistence;


	/**
	 * Enumeración del conjunto de algoritmos posibles de desalojo de entradas del HEAP a disco.
	 */
	public enum MemoryStoreEvictionPolicy {
		LRU("LRU"),
		LFU("LFU"),
		FIFO("FIFO");

		private final String memoryStoreEvictionPolicy;

		/**
		 * Constructor privado del tipo de algoritmo de desalojo.
		 * 
		 * @param memoryStoreEvictionPolicy Cadena de texto aue identifica el algoritmo.
		 */
		private MemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
			this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
		}

		/**
		 * Retorna el nombre con el que se identifica el algoritmo de desalojo.
		 * 
		 * @return el nombre con el que se identifica el algoritmo de desalojo.
		 */
		public String getMemoryStoreEvictionPolicy() {

			return memoryStoreEvictionPolicy;
		}

		/**
		 * Retorna true o false en función de si el algoritmo indicado tiene o no representación en esta enumeración.
		 * 
		 * @param memoryStoreEvictionPolicy Algoritmo a comprobar.
		 * 
		 * @return true o false en función de si el algoritmo indicado tiene o no representación en esta enumeración.
		 */
		public static boolean contains(String memoryStoreEvictionPolicy) {

			for (MemoryStoreEvictionPolicy choice : MemoryStoreEvictionPolicy.values()) {
				if (choice.name().equals(memoryStoreEvictionPolicy)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * Retorna el algoritmo a partir de la cadena de texto indicada.
		 * 
		 * @param text Cadena de texto a partir de la que se deduce el algoritmo de desaolojo.
		 * 
		 * @return el algoritmo a partir de la cadena de texto indicada.
		 */
		public static MemoryStoreEvictionPolicy fromString(String text) {
			if (text != null) {
				for (MemoryStoreEvictionPolicy memoryStoreEvictionPolicy : MemoryStoreEvictionPolicy.values()) {
					if (text.equalsIgnoreCase(memoryStoreEvictionPolicy.memoryStoreEvictionPolicy)) {
						return memoryStoreEvictionPolicy;
					}
				}
			}

			return null;
		}
	}


	/**
	 * Constructor por defecto.
	 */
	public CacheConfiguration() {
	}

	/**
	 * Constructor con parámetros.
	 * 
	 * @param parentConfiguration Configuración padre para los valores por defecto.
	 */
	public CacheConfiguration(CacheConfiguration parentConfiguration) {

		this.parentConfiguration = parentConfiguration;
	}

	/**
	 * Retorna la configuración por defecto establecida por su antecesor.
	 * 
	 * @return la configuración por defecto establecida por su antecesor.
	 */
	public CacheConfiguration getParentConfiguration() {
		return parentConfiguration;
	}

	/**
	 * Asigna la configuración por defecto establecida por su antecesor.
	 * 
	 * @param parentConfiguration La configuración por defecto a asignar.
	 */
	public void setParentConfiguration(CacheConfiguration parentConfiguration) {
		this.parentConfiguration = parentConfiguration;
	}

	/**
	 * Retorna el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 *   
	 * @return el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 */
	public Long getTimeToIdleSeconds() {

		if (timeToIdleSeconds != null) {
			return timeToIdleSeconds;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getTimeToIdleSeconds();
		}

		return DEFAULT_TIME_TO_IDLE_SECONDS; 
	}

	/**
	 * Asigna el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 * 
	 * @param timeToIdleSeconds El tiempo máximo de vida en segundos a asignar.
	 */
	public void setTimeToIdleSeconds(Long timeToIdleSeconds) {
		this.timeToIdleSeconds = timeToIdleSeconds;
	}

	/**
	 * Retorna el tiempo máximo de vida en segundos de un elemento.
	 * 
	 * @return el tiempo máximo de vida en segundos de un elemento.
	 */
	public Long getTimeToLiveSeconds() {

		if (timeToLiveSeconds != null) {
			return timeToLiveSeconds;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getTimeToLiveSeconds();
		}

		return DEFAULT_TIME_TO_LIVE_SECONDS; 
	}

	/**
	 * Asigna el tiempo máximo de vida en segundos de un elemento.
	 * 
	 * @param timeToLiveSeconds El tiempo máximo de vida en segundos a asignar.
	 */
	public void setTimeToLiveSeconds(Long timeToLiveSeconds) {
		this.timeToLiveSeconds = timeToLiveSeconds;
	}

	/**
	 * Retorna true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 * 
	 * Nota: Si esta propiedad se activa, se ignoran los valores de TimeToIdleSeconds y TimeToLiveSeconds.
	 * 
	 * @return true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 */
	public Boolean getEternal() {

		if (eternal != null) {
			return eternal;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getEternal();
		}

		return DEFAULT_ETERNAL; 
	}

	/**
	 * Asigna true o false en función de si los elementos de la caché 
	 * están o no afectados por el paso del tiempo.
	 * 
	 * Nota: Si esta propiedad se activa, se ignoran los valores de TimeToIdleSeconds y TimeToLiveSeconds.
	 * 
	 * @param eternal True o false en función de si los elementos de la caché se consideran o no eternos. 
	 */
	public void setEternal(Boolean eternal) {
		this.eternal = eternal;
	}

	/**
	 * Retorna el número máximo de elementos en memoria.
	 *  
	 * @return el número máximo de elementos en memoria.
	 */
	public Long getMaxEntriesLocalHeap() {

		if (maxEntriesLocalHeap != null) {
			return maxEntriesLocalHeap;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getMaxEntriesLocalHeap();
		}

		return DEFAULT_MAX_ENTRIES_LOCAL_HEAP; 
	}

	/**
	 * Asigna el número máximo de elementos en memoria.
	 * 
	 * @param maxEntriesLocalHeap Número máximo de elementos en memoria.
	 */
	public void setMaxEntriesLocalHeap(Long maxEntriesLocalHeap) {
		this.maxEntriesLocalHeap = maxEntriesLocalHeap;
	}

	/**
	 * Retorna el número máximo de elementos en disco.
	 *  
	 * @return el número máximo de elementos en disco.
	 */
	public Long getMaxEntriesLocalDisk() {

		if (maxEntriesLocalDisk != null) {
			return maxEntriesLocalDisk;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getMaxEntriesLocalDisk();
		}

		return DEFAULT_MAX_ENTRIES_DISK; 
	}

	/**
	 * Asigna el número máximo de elementos en disco.
	 * 
	 * @param maxEntriesLocalDisk Número máximo de elementos en disco.
	 */
	public void setMaxEntriesLocalDisk(Long maxEntriesLocalDisk) {
		this.maxEntriesLocalDisk = maxEntriesLocalDisk;
	}

	/**
	 * Retorna el intervalo de tiempo entre chequeos, en busca de elementos caducados en disco.
	 * 
	 * @return el intervalo de tiempo entre chequeos, en busca de elementos caducados en disco.
	 */
	public Long getDiskExpiryThreadIntervalSeconds() {

		if (diskExpiryThreadIntervalSeconds != null) {
			return diskExpiryThreadIntervalSeconds;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getDiskExpiryThreadIntervalSeconds();
		}

		return DEFAULT_DISK_EXPIRITY_THREAD_INTERVAL_SECONDS; 
	}

	/**
	 * Asigna el intervalo de tiempo entre chequeos, en busca de elementos caducados en disco.
	 * 
	 * @param diskExpiryThreadIntervalSeconds Intervalo de tiempo en segundos
	 */
	public void setDiskExpiryThreadIntervalSeconds(Long diskExpiryThreadIntervalSeconds) {

		this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
	}

	/**
	 * Retorna la política de desalojo de las entradas almacenadas en memoria.
	 * 
	 * @return la política de desalojo de las entradas almacenadas en memoria.
	 */
	public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy() {

		if (memoryStoreEvictionPolicy != null) {
			return memoryStoreEvictionPolicy;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getMemoryStoreEvictionPolicy();
		}

		return DEFAULT_MEMORY_EVICTION_POLICY; 
	}

	/**
	 * Asigna la política de desalojo a usar, de las entradas almacenadas en memoria.
	 * 
	 * @param memoryStoreEvictionPolicy La política de desalojo a usar.
	 */
	public void setMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {

		this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
	}

	/**
	 * Retorna la configuración de persistencia de la caché.
	 * 
	 * @return la configuración de persistencia de la caché.
	 */
	public Persistence getPersistente() {

		if (persistence != null) {
			return persistence;
		}

		if (parentConfiguration != null) {
			return parentConfiguration.getPersistente();
		}

		return DEFAULT_PERSISTENCE; 
	}

	/**
	 * Asigna el estado de la persistencia.
	 * 
	 * @param persistence El nuevo estado de la persistencia.
	 */
	public void setPersistence(Persistence persistence) {
		this.persistence = persistence;
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
	 * @param maxBytesLocalHeap El número máximo de bytes.
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

		return DEFAULT_MAX_BYTES_DISK; 
	}

	/**
	 * Asigna el número máximo de bytes en disco.
	 * 
	 * @param maxBytesLocalDisk El número máximo de bytes.
	 */
	public void setMaxBytesLocalDisk(Long maxBytesLocalDisk) {
		this.maxBytesLocalDisk = maxBytesLocalDisk;
	}

	/**
	 * Retorna la configuración de caché construida.
	 * 
	 * @param name Nombre de la caché para la que se creará la configuración.
	 * 
	 * @return la configuración caché construida.
	 */
	public net.sf.ehcache.config.CacheConfiguration constructCacheConfiguration(String name) {

		net.sf.ehcache.config.CacheConfiguration configuration;

		configuration = new net.sf.ehcache.config.CacheConfiguration();
		configuration.setName(name);
		configuration.setDiskExpiryThreadIntervalSeconds(getDiskExpiryThreadIntervalSeconds());
		configuration.setMemoryStoreEvictionPolicy(getMemoryStoreEvictionPolicy().getMemoryStoreEvictionPolicy());

		configuration.setMaxBytesLocalHeap(getMaxBytesLocalHeap());
		configuration.setMaxEntriesLocalHeap(getMaxEntriesLocalHeap());
		configuration.setMaxBytesLocalDisk(getMaxBytesLocalDisk());
		configuration.setMaxEntriesLocalDisk(getMaxEntriesLocalDisk());
		configuration.setEternal(getEternal());
		configuration.setTimeToIdleSeconds(getTimeToIdleSeconds());
		configuration.setTimeToLiveSeconds(getTimeToLiveSeconds());

		PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
		persistenceConfiguration.setStrategy(getPersistente().isActive() ? "localTempSwap" : "none");
		configuration.persistence(persistenceConfiguration);

		return configuration;
	}

	/**
	 * Retorna la configuración por defecto de una cache.
	 * 
	 * @return la configuración por defecto de una cache.
	 */
	public static net.sf.ehcache.config.CacheConfiguration DefaultCacheConfiguration() {

		net.sf.ehcache.config.CacheConfiguration configuration;

		configuration = new net.sf.ehcache.config.CacheConfiguration();
		configuration.setDiskExpiryThreadIntervalSeconds(DEFAULT_DISK_EXPIRITY_THREAD_INTERVAL_SECONDS);
		configuration.setMemoryStoreEvictionPolicy(DEFAULT_MEMORY_EVICTION_POLICY.getMemoryStoreEvictionPolicy());

		configuration.setMaxBytesLocalHeap(DEFAULT_MAX_BYTES_LOCAL_HEAP);
		configuration.setMaxEntriesLocalHeap(DEFAULT_MAX_ENTRIES_LOCAL_HEAP);
		configuration.setMaxBytesLocalDisk(DEFAULT_MAX_BYTES_DISK);
		configuration.setMaxEntriesLocalDisk(DEFAULT_MAX_ENTRIES_DISK);
		configuration.setEternal(DEFAULT_ETERNAL);
		configuration.setTimeToIdleSeconds(DEFAULT_TIME_TO_IDLE_SECONDS);
		configuration.setTimeToLiveSeconds(DEFAULT_TIME_TO_LIVE_SECONDS);

		PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
		persistenceConfiguration.setStrategy(DEFAULT_PERSISTENCE.isActive() ? "localTempSwap" : "none");
		configuration.persistence(persistenceConfiguration);

		return configuration;
	}

	/**
	 * Retorna una nueva instancia idéntica a esta.
	 * 
	 * @param clone Nueva instancia a poblar, o <code>null</code>.
	 * 
	 * @return la nueva instancia poblada.
	 */
	public CacheConfiguration clone(CacheConfiguration clone) {

		if (clone == null) {
			clone = new CacheConfiguration();
		}

		if (parentConfiguration == null) {
			clone.parentConfiguration = null;
		}
		else {
			clone.parentConfiguration = parentConfiguration.clone(null);
		}

		clone.timeToIdleSeconds = timeToIdleSeconds;
		clone.timeToLiveSeconds = timeToLiveSeconds;
		clone.eternal = eternal;
		clone.maxBytesLocalHeap = maxBytesLocalHeap;
		clone.maxEntriesLocalHeap = maxEntriesLocalHeap;
		clone.maxBytesLocalDisk = maxBytesLocalDisk;
		clone.maxEntriesLocalDisk = maxEntriesLocalDisk;
		clone.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
		clone.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;

		if (persistence == null) {
			clone.persistence = null;
		}
		else {
			clone.persistence = persistence.clone();
		}

		return clone;
	}
}
