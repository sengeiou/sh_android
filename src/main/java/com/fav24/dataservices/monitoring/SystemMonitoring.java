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

	private Timer secondResolutionTimer;
	private Timer minuteResolutionTimer;


	public SystemMonitoring() {

		memoryMeter = new MemoryMeter();
		cpuMeter = new CpuMeter();

		secondResolutionTimer = new Timer("System Second Monitor");

		secondResolutionTimer.schedule(new TimerTask() {

			public void run() {

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
			}
		}, 0L, SECOND_CADENCE);

		storageMeters = new ArrayList<StorageMeter>();
		minuteResolutionTimer = new Timer("System Minute Monitor");

		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			try	{
				storageMeters.add(new StorageMeter(Files.getFileStore(root)));
			}
			catch (IOException e) {
			}
		}

		minuteResolutionTimer.schedule(new TimerTask() {

			public void run() {

				// Storage information.
				for(StorageMeter storageMeter : storageMeters) {

					try {
						SamplesRegister.registerSample(storageMeter, new MonitorSample(storageMeter.getSystemStorageStatus()));
					} catch (ServerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}, 0L, MINUTE_CADENCE);
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
	 */
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long offset, Long timeRange, Long period) {

		return SamplesRegister.getSampleTimeSegment(memoryMeter, offset, timeRange, period);
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
	 */
	public AbstractList<MonitorSample> getSystemCpuActivity(Long offset, Long timeRange, Long period) {

		return SamplesRegister.getSampleTimeSegment(cpuMeter, offset, timeRange, period);
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
	 */
	public AbstractList<MonitorSample> getSystemStorageStatus(String storeName, Long offset, Long timeRange, Long period) {

		for (StorageMeter storageMeter : storageMeters) {

			if (storageMeter.getStoreName().equals(storeName)) {
				SamplesRegister.getSampleTimeSegment(storageMeter, offset, timeRange, period);
			}
		}

		return null;
	}
}
