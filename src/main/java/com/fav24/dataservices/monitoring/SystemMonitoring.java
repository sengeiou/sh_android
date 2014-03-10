package com.fav24.dataservices.monitoring;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;


/**
 * Clase que gestiona y almacena la información de la actividad del sistema. 
 */
public class SystemMonitoring {

	public static final long SECOND_CADENCE = 1000L;
	public static final long MINUTE_CADENCE = 60 * SECOND_CADENCE;

	public static final long MONITORING_TIME_WINDOW = 24 * 60 * 60 * 1000; // Se guardará hasta 24 horas de información.


	public static class MonitorSample implements Comparable<MonitorSample> {

		private Long time;
		private NavigableMap<String, Double> data;


		public MonitorSample() {
			time = null;
			data = null;
		}

		public MonitorSample(NavigableMap<String, Double> data) {

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

		public void setData(String aspect, Double value) {

			if (data == null) {
				data = new TreeMap<String, Double>();
			}

			this.data.put(aspect, value);
		}

		/**
		 *  negative integer, zero, or a positive integer as this object is less than, equal to, or greater than the specified object.
		 */
		public int compareTo(MonitorSample other) {

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
	private AbstractList<MonitorSample> systemMemoryActivityTrace;

	CpuMeter cpuMeter;
	/* Almacenamiento de la actividad de la CPU durante (como máximo) las últimas 24 horas.*/
	private AbstractList<MonitorSample> systemCpuActivityTrace;

	StorageMeter storageMeter;
	/* Almacenamiento de la actividad de los dispositivos de almacemaniento durante (como máximo) las últimas 24 horas.*/
	private NavigableMap<String, AbstractList<MonitorSample>> systemStorageActivityTrace;

	private Timer secondResolutionTimer;
	private Timer minuteResolutionTimer;


	public SystemMonitoring() {

		memoryMeter = new MemoryMeter();
		cpuMeter = new CpuMeter();
		storageMeter = new StorageMeter();

		systemMemoryActivityTrace = new ArrayList<MonitorSample>();
		systemCpuActivityTrace = new ArrayList<MonitorSample>();
		systemStorageActivityTrace = new TreeMap<String, AbstractList<MonitorSample>>();

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
				NavigableMap<String, NavigableMap<String, Double>> systemStorageStatus = storageMeter.getSystemStorageStatus();

				for(Entry<String, NavigableMap<String, Double>> storageElementStatus : systemStorageStatus.entrySet()) {

					putStorageData(storageElementStatus.getKey(), storageElementStatus.getValue());
				}

				for(AbstractList<MonitorSample> storageElement : systemStorageActivityTrace.values()) {
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
	private void putMemorySample(MonitorSample sample) {

		systemMemoryActivityTrace.add(sample);
	}

	/**
	 * Añade una muestra del estado de la carga de proceso del sistema.
	 * 
	 * @param sample Muestra a añadir.
	 */
	private void putCpuSample(MonitorSample sample) {

		systemCpuActivityTrace.add(sample);
	}

	/**
	 * Añade una muestra del estado de los diferentes dispositivos de almacenamiento del sistema.
	 * 
	 * @param sample Muestra a añadir.
	 */
	private void putStorageSample(String storageLocation, MonitorSample sample) {

		AbstractList<MonitorSample> storageElement = systemStorageActivityTrace.get(storageLocation);
		if (storageElement == null) {
			storageElement = new ArrayList<MonitorSample>();

			systemStorageActivityTrace.put(storageLocation, storageElement);
		}

		storageElement.add(sample);
	}

	/**
	 * Añade información del estado de la memoria del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putMemoryData(NavigableMap<String, Double> data) {

		putMemorySample(new MonitorSample(data));
	}

	/**
	 * Añade información del estado de la carga de proceso del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putCpuData(NavigableMap<String, Double> data) {

		putCpuSample(new MonitorSample(data));
	}

	/**
	 * Añade información del estado de los diferentes dispositivos de almacenamiento del sistema.
	 * 
	 * @param data Información a añadir.
	 */
	private void putStorageData(String storageLocation, NavigableMap<String, Double> data) {

		putStorageSample(storageLocation, new MonitorSample(data));
	}

	/**
	 * Elimina del histórico, aquella información anterior a la ventana de tiempo definida.
	 * 
	 * @see {@linkplain #MONITORING_TIME_WINDOW}
	 */
	private void monitorHistoryPurge(AbstractList<MonitorSample> monitorSampleData) {

		long timeEdge = monitorSampleData.get(monitorSampleData.size() - 1).time - MONITORING_TIME_WINDOW;

		Iterator<MonitorSample> monitorSampleDataIterator = monitorSampleData.iterator();
		while(monitorSampleDataIterator.hasNext()) {

			MonitorSample monitorSample = monitorSampleDataIterator.next();

			if (monitorSample.time >= timeEdge) {
				return;
			}
			else {
				monitorSampleDataIterator.remove();
			}
		}
	}

	/**
	 * Retorna el segmento definido por parámetro de la lista de muestras indicada.
	 * 
	 * @param monitorSampleData Conjunto de muestras monitorizadas.
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 a 24 horas.
	 *  
	 * @return el segmento definido por parámetro de la lista de muestras indicada.
	 */
	public AbstractList<MonitorSample> getSampleTimeSegment(AbstractList<MonitorSample> monitorSampleData, Long period, Long timeRange) {

		AbstractList<MonitorSample> timeSegment = new ArrayList<MonitorSample>();
		long lastCapturedSample = monitorSampleData.get(monitorSampleData.size() - 1).time;
		long timeEdge = lastCapturedSample - timeRange;

		long lastSelectedSampleTime = Long.MAX_VALUE;
		for(MonitorSample monitorSample : monitorSampleData) {

			if (monitorSample.time > timeEdge) {

				if (monitorSample.time >= lastSelectedSampleTime + period) {
					lastSelectedSampleTime = monitorSample.time;
					timeSegment.add(monitorSample);
				}
			}
		}

		return timeSegment;
	}

	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 a 24 horas.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public AbstractList<MonitorSample> getSystemMemoryStatus(Long period, Long timeRange) {

		return getSampleTimeSegment(systemMemoryActivityTrace, period, timeRange);
	}

	/**
	 * Retorna la información del estado de la carga de proceso del sistema.
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. De 1 a 24 horas.
	 *  
	 * @return la información del estado de la carga de proceso del sistema.
	 */
	public AbstractList<MonitorSample> getSystemCpuActivity(Long period, Long timeRange) {

		return getSampleTimeSegment(systemCpuActivityTrace, period, timeRange);
	}

	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * @param period Granularidad de la información en segundos. Entre 1 y 3600 segundos.
	 * @param timeRange Rango temporal que se desea obtener en horas. Entre 1 y 24 horas.
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public NavigableMap<String, AbstractList<MonitorSample>> getSystemStorageStatus(Long period, Long timeRange) {

		NavigableMap<String, AbstractList<MonitorSample>> timeSegment = new TreeMap<String, AbstractList<MonitorSample>>();

		for (Entry<String, AbstractList<MonitorSample>> monitorSampleData : systemStorageActivityTrace.entrySet()) {
			
			timeSegment.put(monitorSampleData.getKey(), getSampleTimeSegment(monitorSampleData.getValue(), period, timeRange));
		}

		return timeSegment;
	}
}
