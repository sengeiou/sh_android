package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.ehcache.CacheManager;

import com.fav24.dataservices.domain.cache.EntityCacheManager;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.meter.CacheMeter;
import com.fav24.dataservices.monitoring.meter.CpuMeter;
import com.fav24.dataservices.monitoring.meter.MemoryMeter;
import com.fav24.dataservices.monitoring.meter.StorageMeter;
import com.fav24.dataservices.monitoring.meter.WorkloadMeter;


/**
 * Clase que gestiona y almacena la información de la actividad del sistema. 
 */
public class SystemMonitoring extends Timer {

	public static final long SECOND_CADENCE = 1000L;
	public static final long MINUTE_CADENCE = 60 * SECOND_CADENCE;

	public static final long MONITORING_TIME_WINDOW = 24 * 60 * 60 * 1000; // Se guardará hasta 24 horas de información.

	private MemoryMeter memoryMeter;
	private CpuMeter cpuMeter;
	private AbstractList<CacheMeter> cacheMeters;
	private AbstractList<StorageMeter> storageMeters;
	private WorkloadMeter workloadMeter;

	private int counter;


	public SystemMonitoring() {

		super("System Monitor");

		memoryMeter = new MemoryMeter();
		cpuMeter = new CpuMeter();
		workloadMeter = new WorkloadMeter();
		cacheMeters = new ArrayList<CacheMeter>();

		storageMeters = new ArrayList<StorageMeter>();
		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			try	{
				storageMeters.add(new StorageMeter(Files.getFileStore(root)));
			}
			catch (IOException e) {
			}
		}

		counter = 0;

		schedule(new TimerTask() {

			public void run() {

				long threadId = Thread.currentThread().getId();

				cpuMeter.excludeThread(threadId);

				// Memory information.
				try {
					SamplesRegister.registerSample(memoryMeter, new MonitorSample(memoryMeter.getSystemMemoryStatus()));
				} catch (Throwable t) {
					t.printStackTrace();
				}

				// CPU information.
				try {
					SamplesRegister.registerSample(cpuMeter, new MonitorSample(cpuMeter.getSystemCpuActivity()));
				} catch (Throwable t) {
					t.printStackTrace();
				}

				// Workload information.
				try {
					SamplesRegister.registerSample(workloadMeter, new MonitorSample(workloadMeter.getSystemWorkload()));
				} catch (Throwable t) {
					t.printStackTrace();
				}

				// Caché information.
				synchronized (cacheMeters) {
					for(CacheMeter cacheMeter : cacheMeters) {

						try {
							SamplesRegister.registerSample(cacheMeter, new MonitorSample(cacheMeter.getCacheStatus()));
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}

				// Ejecuciones cada 10 segundos.
				if (counter % 10 == 0) {
					// Storage information.
					for(StorageMeter storageMeter : storageMeters) {

						try {
							SamplesRegister.registerSample(storageMeter, new MonitorSample(storageMeter.getSystemStorageStatus()));
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}

					counter = 1;
				}
				else {
					counter ++;
				}

				cpuMeter.includeThread(threadId);
			}
		}, 0L, SECOND_CADENCE);
	}

	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual
	 * en este mismo instante.
	 * 
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public MonitorSample getSystemMemoryStatus() {

		return SamplesRegister.getLastSample(memoryMeter);
	}

	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 * 
	 * Nota: timeRange debe ser superior a period.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long offset, Long timeRange, Long period) throws ServerException {

		return SamplesRegister.getSampleTimeSegment(memoryMeter, offset, timeRange, period);
	}

	/**
	 * Retorna el medidor de CPU del sistema.
	 * 
	 * @return el medidor de CPU del sistema.
	 */	
	public CpuMeter getCpuMeter() {

		return cpuMeter;
	}

	/**
	 * Retorna la información del estado de la carga de proceso del sistema
	 * en este mismo instante.
	 * 
	 * @return la información del estado de la carga de proceso del sistema.
	 */
	public MonitorSample getSystemCpuActivity() {

		return SamplesRegister.getLastSample(cpuMeter);
	}

	/**
	 * Retorna la información del estado de la carga de proceso del sistema.
	 * 
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * @return la información del estado de la carga de proceso del sistema.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemCpuActivity(Long offset, Long timeRange, Long period) throws ServerException {

		return SamplesRegister.getSampleTimeSegment(cpuMeter, offset, timeRange, period);
	}

	/**
	 * Retorna el medidor de trabajo realizado por el sistema.
	 * 
	 * @return el medidor de trabajo realizado por el sistema.
	 */
	public WorkloadMeter getWorkloadMeter() {
		return workloadMeter;
	}

	/**
	 * Retorna la información del trabajo realizado por el sistema
	 * en este mismo instante.
	 * 
	 * @return la información del trabajo realizado por el sistema.
	 */
	public MonitorSample getSystemWorkload() {

		return SamplesRegister.getLastSample(workloadMeter);
	}

	/**
	 * Retorna la información del trabajo realizado por el sistema
	 * 
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 *  
	 * @return la información del trabajo realizado por el sistema
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemWorkload(Long offset, Long timeRange, Long period) throws ServerException {

		return SamplesRegister.getSampleTimeSegment(workloadMeter, offset, timeRange, period);
	}


	/**
	 * Retorna el medidor de trabajo realizado por el sistema.
	 * 
	 * @param copyTo Lista en donde que copiarían los medidores de caché actuales. (Puede ser null).
	 * 
	 * @return el medidor de trabajo realizado por el sistema.
	 */
	public AbstractList<CacheMeter> getCacheMeters(AbstractList<Meter> copyTo) {

		synchronized (cacheMeters) {

			if (copyTo != null) {
				copyTo.clear();
				copyTo.addAll(cacheMeters);
			}
		}

		return cacheMeters;
	}

	/**
	 * Actualiza los medidores de caché a partir de la lista de gestores de caché suministrada.
	 * 
	 * @param entityCacheManagers Lista de gestores de caché.
	 */
	public void updateCacheMeters(AbstractList<EntityCacheManager> entityCacheManagers) {

		synchronized (cacheMeters) {
			cacheMeters.clear();
		}
		
		if (entityCacheManagers != null) {
			
			for (EntityCacheManager entityCacheManager : entityCacheManagers) {

				CacheManager cacheManager = entityCacheManager.getCacheManager();

				for (String cacheName : cacheManager.getCacheNames())
				{
					synchronized (cacheMeters) {

						cacheMeters.add(new CacheMeter(cacheManager.getCache(cacheName)));
					}
				}
			}
		}
	}

	/**
	 * Retorna la información asociada a la caché indicada, en este mismo instante.
	 * 
	 * @param cacheManagerName Nombre del gestor de caché al que pertenece la caché de la que se desea obtener la información.
	 * @param cacheName Nombre de la caché de la que se desea obtener la información.
	 * 
	 * @return la información asociada a la caché indicada, en este mismo instante.
	 */
	public MonitorSample getSystemCacheStatus(String cacheManagerName, String cacheName) {

		synchronized (cacheMeters) {

			for (CacheMeter cacheMeter : cacheMeters) {

				if (cacheMeter.getCacheManagerName().equals(cacheManagerName) && cacheMeter.getCacheName().equals(cacheName)) {
					return SamplesRegister.getLastSample(cacheMeter);
				}
			}
		}

		return null;
	}

	/**
	 * Retorna la información asociada a la caché indicada, para el periodo especificado.
	 * 
	 * @param cacheManagerName Nombre del gestor de caché al que pertenece la caché de la que se desea obtener la información.
	 * @param cacheName Nombre de la caché de la que se desea obtener la información.
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 * 
	 * @return la información asociada a la caché indicada, para el periodo especificado.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemCacheStatus(String cacheManagerName, String cacheName, Long offset, Long timeRange, Long period) throws ServerException {

		synchronized (cacheMeters) {

			for (CacheMeter cacheMeter : cacheMeters) {

				if (cacheMeter.getCacheManagerName().equals(cacheManagerName) && cacheMeter.getCacheName().equals(cacheName)) {
					return SamplesRegister.getSampleTimeSegment(cacheMeter, offset, timeRange, period);
				}
			}
		}

		return null;
	}

	/**
	 * Retorna la información asociada al elemento de almacenamiento indicado, en este mismo instante.
	 * 
	 * @param storeName Nombre del almacén del que se desea obtener la información.
	 * 
	 * @return la información asociada al elemento de almacenamiento indicado, en este mismo instante.
	 */
	public MonitorSample getSystemStorageStatus(String storeName) {

		for (StorageMeter storageMeter : storageMeters) {

			if (storageMeter.getStoreName().equals(storeName)) {
				return SamplesRegister.getLastSample(storageMeter);
			}
		}

		return null;
	}

	/**
	 * Retorna la información asociada al elemento de almacenamiento indicado, para el periodo especificado.
	 * 
	 * @param storeName Nombre del almacén del que se desea obtener la información.
	 * @param offset Inicio del corte temporal a obtener en segundos desde epoch.
	 * @param timeRange Rango temporal que se desea obtener en segundos.
	 * @param period Granularidad de la información en segundos.
	 * 
	 * @return la información asociada al elemento de almacenamiento indicado, para el periodo especificado.
	 * 
	 * @throws ServerException 
	 */
	public AbstractList<MonitorSample> getSystemStorageStatus(String storeName, Long offset, Long timeRange, Long period) throws ServerException {

		for (StorageMeter storageMeter : storageMeters) {

			if (storageMeter.getStoreName().equals(storeName)) {
				return SamplesRegister.getSampleTimeSegment(storageMeter, offset, timeRange, period);
			}
		}

		return null;
	}
}
