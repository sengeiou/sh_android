package com.fav24.dataservices.monitoring;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Clase que gestiona y almacena la información de la actividad del sistema. 
 */
public class SystemMonitoring {

	public static final String MAX_MEMORY = "MaxMemory"; //Memória máxima a usar. Se configura mediante el parámetro - Xmx
	public static final String TOTAL_MEMORY = "TotalMemory"; //Hasta donde se puede expandir el heap. Se configura mediante el parámetro -Xms
	public static final String FREE_MEMORY = "FreeMemory"; //Memoria disponible en el sistema. 
	public static final String USED_MEMORY = "UsedMemory"; //Memoria en uso. 

	public static final String TOTAL_STORAGE_SPACE = "TotalStorageSpace"; //Capacidad total de almacenamiento.
	public static final String TOTAL_USABLE_STORAGE_SPACE = "TotalUsableStorageSpace"; //Espacio disponible de almacenamiento.


	public static class MonitorSampleData implements Comparable<MonitorSampleData> {

		private Long time;
		private Map<String, Long> data;


		public MonitorSampleData() {
			time = null;
			data = null;
		}

		public MonitorSampleData(Map<String, Long> data) {

			this.time = System.currentTimeMillis();
			this.data = data;
		}

		public Long getTime() {
			return time;
		}

		public void setTime(Long time) {
			this.time = time;
		}

		public Long getData(String aspect) {
			return data != null ? data.get(aspect) : null;
		}

		public void setData(Map<String, Long> data) {
			this.data = data;
		}

		public void setData(String aspect, Long value) {

			if (data == null) {
				data = new HashMap<String, Long>();
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


	private AbstractList<MonitorSampleData> systemMemoryActivity;
	private Map<String, AbstractList<MonitorSampleData>> systemStorageActivity;


	public SystemMonitoring() {

		systemMemoryActivity = new ArrayList<MonitorSampleData>();
		systemStorageActivity = new HashMap<String, AbstractList<MonitorSampleData>>();
	}

	public void putMemorySample(MonitorSampleData sample) {

		systemMemoryActivity.add(sample);
	}

	public void putStorageSample(String storageLocation, MonitorSampleData sample) {

		AbstractList<MonitorSampleData> storageElement = systemStorageActivity.get(storageLocation);
		if (storageElement == null) {
			storageElement = new ArrayList<MonitorSampleData>();

			systemStorageActivity.put(storageLocation, storageElement);
		}

		storageElement.add(sample);
	}

	public void putMemoryData(Map<String, Long> data) {

		putMemorySample(new MonitorSampleData(data));
	}

	public void putStorageData(String storageLocation, Map<String, Long> data) {

		putStorageSample(storageLocation, new MonitorSampleData(data));
	}
	
	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public Map<String, Long> getSystemMemoryStatus() {

		Map<String, Long> systemMemoryStatus = new HashMap<String, Long>();

		long maxMemory = Runtime.getRuntime().maxMemory(); 
		long totalMemory = Runtime.getRuntime().totalMemory();
		long usedMemory = totalMemory - Runtime.getRuntime().freeMemory(); 
		long freeMemory = maxMemory - usedMemory;

		systemMemoryStatus.put(MAX_MEMORY, maxMemory);
		systemMemoryStatus.put(TOTAL_MEMORY, totalMemory);
		systemMemoryStatus.put(USED_MEMORY, usedMemory);
		systemMemoryStatus.put(FREE_MEMORY, freeMemory);

		return systemMemoryStatus;
	}

	/**
	 * Retorna un mapa con el conjunto de elementos de almacenamiento, y su información asociada en cuanto a:
	 * 
	 * {@linkplain #TOTAL_STORAGE_SPACE}
	 * {@linkplain #TOTAL_USABLE_STORAGE_SPACE}
	 * 
	 * @return un mapa con el conjunto de elementos de almacenamiento, y su información asociada.
	 */
	public Map<String, Map<String, Long>> getSystemStorageStatus() {

		Map<String, Map<String, Long>> systemStorageStatus = new HashMap<String, Map<String, Long>>();

		for (Path root : FileSystems.getDefault().getRootDirectories())
		{
			Map<String, Long> storageStatus = new HashMap<String, Long>();

			try	{

				FileStore store = Files.getFileStore(root);

				storageStatus.put(TOTAL_STORAGE_SPACE, store.getTotalSpace());
				storageStatus.put(TOTAL_USABLE_STORAGE_SPACE, store.getUsableSpace());

				systemStorageStatus.put(root.toString(), storageStatus);
			}
			catch (IOException e) {
			}
		}

		return systemStorageStatus;
	}
}