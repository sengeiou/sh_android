package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CacheConfigurationDOM
{
	private Long timeToIdleSeconds;
	private Long timeToLiveSeconds;
	private Boolean eternal;
	private Long maxBytesLocalHeap;
	private Long maxEntriesLocalHeap;
	private Long maxBytesLocalDisk;
	private Long maxEntriesLocalDisk;
	private Long diskExpiryThreadIntervalSeconds;
	private MemoryStoreEvictionPolicy memoryStoreEvictionPolicy;
	private PersistenceDOM persistence;


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
	 * Construye la configuración de una caché.
	 * 
	 * @param node Nodo de inicio de una caché.
	 */
	public CacheConfigurationDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("TimeToIdleSeconds".equals(nodeName)) {

					timeToIdleSeconds = Long.parseLong(node_i.getTextContent());
				}
				else if ("TimeToLiveSeconds".equals(nodeName)) {

					timeToLiveSeconds = Long.parseLong(node_i.getTextContent());
				}
				else if ("Eternal".equals(nodeName)) {

					eternal = Boolean.parseBoolean(node_i.getTextContent());
				}
				else if ("MaxBytesLocalHeap".equals(nodeName)) {

					maxBytesLocalHeap = Long.parseLong(node_i.getTextContent());
				}
				else if ("MaxEntriesLocalHeap".equals(nodeName)) {

					maxEntriesLocalHeap = Long.parseLong(node_i.getTextContent());
				}
				else if ("MaxBytesLocalDisk".equals(nodeName)) {

					maxBytesLocalDisk = Long.parseLong(node_i.getTextContent());
				}
				else if ("MaxEntriesLocalDisk".equals(nodeName)) {

					maxEntriesLocalDisk = Long.parseLong(node_i.getTextContent());
				}
				else if ("DiskExpiryThreadIntervalSeconds".equals(nodeName)) {

					diskExpiryThreadIntervalSeconds = Long.parseLong(node_i.getTextContent());
				}
				else if ("MemoryStoreEvictionPolicy".equals(nodeName)) {

					memoryStoreEvictionPolicy = MemoryStoreEvictionPolicy.fromString(node_i.getTextContent());
				}
				else if ("Persistence".equals(nodeName)) {

					persistence = new PersistenceDOM(node_i);
				}
			}
		}
	}

	/**
	 * Retorna el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 *   
	 * @return el tiempo máximo de vida en segundos de un elemento que no está siendo consultado.
	 */
	public Long getTimeToIdleSeconds() {
		return timeToIdleSeconds;
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
		return timeToLiveSeconds;
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
		return eternal;
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
		return maxEntriesLocalHeap;
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
		return maxEntriesLocalDisk;
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
		return diskExpiryThreadIntervalSeconds;
	}

	/**
	 * Asigna el intervalo de tiempo entre chequeos, en busca de elementos caducados en disco.
	 * 
	 * @param diskExpiryThreadIntervalSeconds Intervalo de tiempo en segundos
	 */
	public void setDiskExpiryThreadIntervalSeconds(
			Long diskExpiryThreadIntervalSeconds) {
		this.diskExpiryThreadIntervalSeconds = diskExpiryThreadIntervalSeconds;
	}

	/**
	 * Retorna la política de desalojo de las entradas almacenadas en memoria.
	 * 
	 * @return la política de desalojo de las entradas almacenadas en memoria.
	 */
	public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy() {
		return memoryStoreEvictionPolicy;
	}

	/**
	 * Asigna la política de desalojo a usar, de las entradas almacenadas en memoria.
	 * 
	 * @param memoryStoreEvictionPolicy La política de desalojo a usar.
	 */
	public void setMemoryStoreEvictionPolicy(
			MemoryStoreEvictionPolicy memoryStoreEvictionPolicy) {
		this.memoryStoreEvictionPolicy = memoryStoreEvictionPolicy;
	}

	/**
	 * Retorna la configuración de persistencia de la caché.
	 * 
	 * @return la configuración de persistencia de la caché.
	 */
	public PersistenceDOM getPersistente() {
		return persistence;
	}

	/**
	 * Asigna el estado de la persistencia.
	 * 
	 * @param persistence El nuevo estado de la persistencia.
	 */
	public void setPersistence(PersistenceDOM persistence) {
		this.persistence = persistence;
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
		return maxBytesLocalDisk;
	}

	/**
	 * Asigna el número máximo de bytes en disco.
	 * 
	 * @param maxBytesLocalDisk El número máximo de bytes.
	 */
	public void setMaxBytesLocalDisk(Long maxBytesLocalDisk) {
		this.maxBytesLocalDisk = maxBytesLocalDisk;
	}
}
