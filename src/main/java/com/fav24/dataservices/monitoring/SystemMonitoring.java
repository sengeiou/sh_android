package com.fav24.dataservices.monitoring;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Clase que gestiona y almacena la información de la actividad del sistema. 
 */
public class SystemMonitoring {

	public static final long SECOND_CADENCE = 1000L;
	public static final long MINUTE_CADENCE = 60 * SECOND_CADENCE;

	public static final long MONITORING_TIME_WINDOW = 24 * 60 * 60 * 1000; // Se guardará hasta 24 horas de información.


	public static class MonitorSampleData implements Comparable<MonitorSampleData> {

		private Long time;
		private Map<String, Double> data;


		public MonitorSampleData() {
			time = null;
			data = null;
		}

		public MonitorSampleData(Map<String, Double> data) {

			this.time = System.currentTimeMillis();
			this.data = data;
		}

		public Long getTime() {
			return time;
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public Double getData(String aspect) {
			return data != null ? data.get(aspect) : null;
		}

		public void setData(Map<String, Double> data) {
			this.data = data;
		}

		public void setData(String aspect, Double value) {

			if (data == null) {
				data = new HashMap<String, Double>();
			}

			this.data.put(aspect, value);
		}

		/**
		 *  negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
		 */
		public int compareTo(MonitorSampleData other) {

			if (time == other.time) {
				return 0;
			}

			if (time != null) {

				if (other.time != null) {

					return time < other.time ? -1 : 1;
				}
				else {
					return 1;
				}
			}
			else {
				return -1;
			}
		}
	}

	MemoryMeter memoryMeter;
	/* Almacenamiento de la actividad de memoria durante (como máximo) las últimas 24 horas.*/
	private AbstractList<MonitorSampleData> systemMemoryActivityTrace;

	CpuMeter cpuMeter;
	/* Almacenamiento de la actividad de la CPU durante (como máximo) las últimas 24 horas.*/
	private AbstractList<MonitorSampleData> systemCpuActivityTrace;

	StorageMeter storageMeter;
	/* Almacenamiento de la actividad de los dispositivos de almacemaniento durante (como máximo) las últimas 24 horas.*/
	private Map<String, AbstractList<MonitorSampleData>> systemStorageActivityTrace;

	private Timer secondResolutionTimer;
	private Timer minuteResolutionTimer;


	public SystemMonitoring() {

		memoryMeter = new MemoryMeter();
		cpuMeter = new CpuMeter();
		storageMeter = new StorageMeter();

		systemMemoryActivityTrace = new ArrayList<MonitorSampleData>();
		systemCpuActivityTrace = new ArrayList<MonitorSampleData>();
		systemStorageActivityTrace = new HashMap<String, AbstractList<MonitorSampleData>>();

		secondResolutionTimer = new Timer("System Second Monitor");
		minuteResolutionTimer = new Timer("System Minute Monitor");

		secondResolutionTimer.schedule(new TimerTask() {

			public void run() {

				// Memory information.
				putMemoryData(memoryMeter.getSystemMemoryStatus());

				// CPU information.
				putCpuData(cpuMeter.getSystemCpuActivity());

				monitorHistoryPurge(systemMemoryActivityTrace);
				monitorHistoryPurge(systemCpuActivityTrace);
			}
		}, 0L, SECOND_CADENCE);

		minuteResolutionTimer.schedule(new TimerTask() {

			public void run() {

				// Storage information.
				Map<String, Map<String, Double>> systemStorageStatus = storageMeter.getSystemStorageStatus();

				for(Entry<String, Map<String, Double>> storageElementStatus : systemStorageStatus.entrySet()) {

					putStorageData(storageElementStatus.getKey(), storageElementStatus.getValue());
				}

				for(AbstractList<MonitorSampleData> storageElement : systemStorageActivityTrace.values()) {
					monitorHistoryPurge(storageElement);
				}
			}
		}, 0L, MINUTE_CADENCE);
	}

	/**
	 * Añade una muestra del estado de la memoria del sistema.
	 * 
	 * @param sample Muestra a añadir.
	 */
	private void putMemorySample(MonitorSampleData sample) {

		systemMemoryActivityTrace.add(sample);
	}

	/**
	 * Añade una muestra del estado de la carga de proceso del sistema.
	 * 
	 * @param sample Muestra a añadir.
	 */
	private void putCpuSample(MonitorSampleData sample) {

		systemCpuActivityTrace.add(sample);
	}

	/**
	 * Añade una muestra del estado de los diferentes dispositivos de almacenamiento del sistema.
	 * 
	 * @param sample Muestra a añadir.
	 */
	private void putStorageSample(String storageLocation, MonitorSampleData sample) {

		AbstractList<MonitorSampleData> storageElement = systemStorageActivityTrace.get(storageLocation);
		if (storageElement == null) {
			storageElement = new ArrayList<MonitorSampleData>();

			systemStorageActivityTrace.put(storageLocation, storageElement);
		}

		storageElement.add(sample);
	}

	/**
	 * Añade información del estado de la memoria del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putMemoryData(Map<String, Double> data) {

		putMemorySample(new MonitorSampleData(data));
	}

	/**
	 * Añade información del estado de la carga de proceso del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putCpuData(Map<String, Double> data) {

		putCpuSample(new MonitorSampleData(data));
	}

	/**
	 * Añade información del estado de los diferentes dispositivos de almacenamiento del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putStorageData(String storageLocation, Map<String, Double> data) {

		putStorageSample(storageLocation, new MonitorSampleData(data));
	}

	/**
	 * Elimina del histórico, aquella información anterior a la ventana de tiempo definida.
	 * 
	 * @see {@linkplain #MONITORING_TIME_WINDOW}
	 */
	private void monitorHistoryPurge(AbstractList<MonitorSampleData> monitorSampleData) {

		long timeEdge = monitorSampleData.get(monitorSampleData.size() - 1).time - MONITORING_TIME_WINDOW;

		Iterator<MonitorSampleData> monitorSampleDataIterator = monitorSampleData.iterator();
		while(monitorSampleDataIterator.hasNext()) {

			MonitorSampleData monitorSample = monitorSampleDataIterator.next();

			if (monitorSample.time >= timeEdge) {
				return;
			}
			else {
				monitorSampleDataIterator.remove();
			}
		}
	}
	
	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 a 24 horas.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public AbstractList<MonitorSampleData> getSystemMemoryStatus(Long period, Long timeRange) {


		return null;
	}

	/**
	 * Retorna la información del estado de la carga de proceso del sistema.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 a 24 horas.
	 *  
	 * @return la información del estado de la carga de proceso del sistema.
	 */
	public AbstractList<MonitorSampleData> getSystemCpuActivity(Long period, Long timeRange) {


		return null;
	}

	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 y 24 horas.
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, AbstractList<MonitorSampleData>> getSystemStorageStatus(Long period, Long timeRange) {
		
		return null;
	}
}
