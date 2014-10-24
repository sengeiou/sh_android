package com.fav24.dataservices.monitoring.meter;

import java.util.NavigableMap;
import java.util.TreeMap;

import net.sf.ehcache.Cache;
import net.sf.ehcache.statistics.StatisticsGateway;

import com.fav24.dataservices.monitoring.Meter;

/**
 * Clase para obtener el estado de la caché.
 */
public final class CacheMeter extends Meter
{

	public static final String TOTAL_HEAP_SPACE = "TotalHeapSpace"; //Capacidad total del fragmento de heap asociado a la caché.
	public static final String HEAP_USED_SPACE = "UsedHeapSpace"; //Espacio usado del fragmento de heap.
	public static final String TOTAL_DISK_SPACE = "TotalDiskSpace"; //Capacidad total del fragmento de disco asociado a la caché.
	public static final String DISK_USED_SPACE = "UsedDiskSpace"; //Espacio usado del fragmento de disco.
	public static final String TOTAL_HEAP_HIT = "TotalHeapHit"; //Número de aciertos en memoria.
	public static final String TOTAL_HEAP_MISS = "TotalHeapMiss"; //Número de fallos en memoria.
	public static final String TOTAL_HEAP_HIT_RATIO = "TotalHeapHitRatio"; //Proporción de aciertos en memoria.
	public static final String TOTAL_DISK_HIT = "TotalDiskHit"; //Número de aciertos en disco.
	public static final String TOTAL_DISK_MISS = "TotalDiskMiss"; //Número de fallos en disco.
	public static final String TOTAL_DISK_HIT_RATIO = "TotalDiskHitRatio"; //Proporción de aciertos en disco.

	private Cache cache;
	private String cacheMeterName;


	/** 
	 * Constructor.
	 * 
	 * @param cache Caché de entidad de la que se obtendrán las medidas.
	 */
	public CacheMeter(Cache cache) {

		this.cache = cache;
		this.cacheMeterName = "cache-meter_" + getCacheManagerName() + "_" + getCacheName();
	}

	/**
	 * Retorna un mapa con la información asociada a la caché de entidad.
	 * 
	 * {@linkplain #TOTAL_HEAP_SPACE}
	 * {@linkplain #HEAP_USED_SPACE}
	 * {@linkplain #TOTAL_DISK_SPACE}
	 * {@linkplain #DISK_USED_SPACE}
	 * 
	 * {@linkplain #TOTAL_HEAP_HIT}
	 * {@linkplain #TOTAL_HEAP_MISS}
	 * {@linkplain #TOTAL_HEAP_HIT_RATIO}
	 * {@linkplain #TOTAL_DISK_HIT}
	 * {@linkplain #TOTAL_DISK_MISS}
	 * {@linkplain #TOTAL_DISK_HIT_RATIO}
	 * 
	 * @return un mapa con la información asociada a la caché de entidad.
	 */
	public NavigableMap<String, Double> getCacheStatus() {

		NavigableMap<String, Double> cacheStatus = new TreeMap<String, Double>();

		StatisticsGateway cacheStatistics = cache.getStatistics();

		// Uso de memoria.
		cacheStatus.put(TOTAL_HEAP_SPACE, Double.valueOf(cache.getCacheConfiguration().getMaxBytesLocalHeap()));
		cacheStatus.put(HEAP_USED_SPACE, Double.valueOf(cacheStatistics.getLocalHeapSizeInBytes()));

		// Uso de disco.
		cacheStatus.put(TOTAL_DISK_SPACE, Double.valueOf(cache.getCacheConfiguration().getMaxBytesLocalDisk()));
		cacheStatus.put(DISK_USED_SPACE, Double.valueOf(cacheStatistics.getLocalDiskSizeInBytes()));

		// Hit/Miss Heap.
		double totalHeapHit = Double.valueOf(cacheStatistics.localHeapHitCount());
		double totalHeapMiss= Double.valueOf(cacheStatistics.localHeapMissCount());
		cacheStatus.put(TOTAL_HEAP_HIT, totalHeapHit);
		cacheStatus.put(TOTAL_HEAP_MISS, totalHeapMiss);
		double heapHitRatio = totalHeapHit == 0 ? 0 : ((totalHeapMiss + totalHeapHit)*100)/totalHeapHit ;
		cacheStatus.put(TOTAL_HEAP_HIT_RATIO, heapHitRatio);

		// Hit/Miss Disk.
		double totalDiskHit = Double.valueOf(cacheStatistics.localDiskHitCount());
		double totalDiskMiss= Double.valueOf(cacheStatistics.localDiskMissCount());
		cacheStatus.put(TOTAL_DISK_HIT, totalDiskHit);
		cacheStatus.put(TOTAL_DISK_MISS, totalDiskMiss);
		double diskHitRatio = totalDiskHit == 0 ? 0 : ((totalDiskMiss + totalDiskHit)*100)/totalDiskHit ;
		cacheStatus.put(TOTAL_DISK_HIT_RATIO, diskHitRatio);

		return cacheStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMeterName() {
		return cacheMeterName;
	}

	/**
	 * Retorna el nombre de la caché a la que está asociada este medidor.
	 * 
	 * @return el nombre de la caché a la que está asociada este medidor.
	 */
	public String getCacheManagerName() {
		return cache.getCacheManager().getName();
	}

	/**
	 * Retorna el nombre de la caché a la que está asociada este medidor.
	 * 
	 * @return el nombre de la caché a la que está asociada este medidor.
	 */
	public String getCacheName() {
		return cache.getName();
	}
}