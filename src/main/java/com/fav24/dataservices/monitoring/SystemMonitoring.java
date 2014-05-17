package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.meter.CpuMeter;
import com.fav24.dataservices.monitoring.meter.MemoryMeter;
import com.fav24.dataservices.monitoring.meter.StorageMeter;
import com.fav24.dataservices.monitoring.meter.WorkloadMeter;


/**
 * Clase que gestiona y almacena la información de la actividad del sistema. 
 */
public class SystemMonitoring {

	public static final long SECOND_CADENCE = 1000L;
	public static final long MINUTE_CADENCE = 60 * SECOND_CADENCE;

	public static final long MONITORING_TIME_WINDOW = 24 * 60 * 60 * 1000; // Se guardará hasta 24 horas de información.



	private MemoryMeter memoryMeter;
	private CpuMeter cpuMeter;
	private AbstractList<StorageMeter> storageMeters;
	private WorkloadMeter workloadMeter;

	private Timer secondResolutionTimer;
	private Timer tenSecondsResolutionTimer;


	public SystemMonitoring() {

		memoryMeter = new MemoryMeter();
		cpuMeter = new CpuMeter();
		workloadMeter = new WorkloadMeter();

		secondResolutionTimer = new Timer("System Second Monitor");

		secondResolutionTimer.schedule(new TimerTask() {

			public void run() {

				long threadId = Thread.currentThread().getId();
				
				cpuMeter.excludeThread(threadId);

				// Memory information.
				try {
					SamplesRegister.registerSample(memoryMeter, new MonitorSample(memoryMeter.getSystemMemoryStatus()));
				} catch (ServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// CPU information.
				try {
					SamplesRegister.registerSample(cpuMeter, new MonitorSample(cpuMeter.getSystemCpuActivity()));
				} catch (ServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// Workload information.
				try {
					SamplesRegister.registerSample(workloadMeter, new MonitorSample(workloadMeter.getSystemWorkload()));
				} catch (ServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				cpuMeter.includeThread(threadId);
			}
		}, 0L, SECOND_CADENCE);

		storageMeters = new ArrayList<StorageMeter>();
		tenSecondsResolutionTimer = new Timer("System Minute Monitor");

		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			try	{
				storageMeters.add(new StorageMeter(Files.getFileStore(root)));
			}
			catch (IOException e) {
			}
		}

		tenSecondsResolutionTimer.schedule(new TimerTask() {

			public void run() {
				
				long threadId = Thread.currentThread().getId();
				
				cpuMeter.excludeThread(threadId);
				
				// Storage information.
				for(StorageMeter storageMeter : storageMeters) {

					try {
						SamplesRegister.registerSample(storageMeter, new MonitorSample(storageMeter.getSystemStorageStatus()));
					} catch (ServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				cpuMeter.includeThread(threadId);
			}
		}, 0L, SECOND_CADENCE * 10);
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
	 * Retorna la información asociada al elemento de almacenamiento indicado, en este mismos instante.
	 * 
	 * @param storeName Nombre del almacén del que se desea obtener la información.
	 * 
	 * @return la información asociada al elemento de almacenamiento indicado, en este mismos instante.
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
