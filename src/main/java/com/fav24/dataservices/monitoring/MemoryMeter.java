package com.fav24.dataservices.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

/**
 * Clase para trazar el uso de la memoria
 * de la aplicación.
 */
public final class MemoryMeter {

	public static final String MAX_MEMORY = "MaxMemory"; //Memória máxima a usar. Se configura mediante el parámetro - Xmx
	public static final String TOTAL_MEMORY = "TotalMemory"; //Hasta donde se puede expandir el heap. Se configura mediante el parámetro -Xms
	public static final String FREE_MEMORY = "FreeMemory"; //Memoria disponible en el sistema. 
	public static final String USED_MEMORY = "UsedMemory"; //Memoria en uso.
	public static final String USED_HEAP_MEMORY = "UsedHeapMemory"; //Memoria en uso por instancias de objetos.
	public static final String USED_NONHEAP_MEMORY = "UsedNonHeapMemory"; //Memoria en uso por estructuras internas a la ejecución. 


	/** 
	 * Constructor por defecto.
	 */
	public MemoryMeter() {

	}

	/**
	 * Retorna la información de estado de la memoria, en la máquina virtual.
	 *  
	 * @return la información de estado de la memoria, en la máquina virtual.
	 */
	public Map<String, Double> getSystemMemoryStatus() {

		Map<String, Double> systemMemoryStatus = new HashMap<String, Double>();

		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean(); 
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage(); 
		MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage(); 

		double maxMemory = heapMemoryUsage.getMax();
		if (maxMemory >= 0) {
			if (nonHeapMemoryUsage.getMax() >= 0) {
				maxMemory += nonHeapMemoryUsage.getMax();
			}
			else {
				maxMemory = -1;
			}
		}

		double totalMemory = heapMemoryUsage.getCommitted() + nonHeapMemoryUsage.getCommitted();
		double usedHeapMemory = heapMemoryUsage.getUsed(); 
		double usedNonHeapMemory = nonHeapMemoryUsage.getUsed(); 
		double usedMemory = usedHeapMemory + usedNonHeapMemory; 
		double freeMemory = totalMemory - usedMemory;

		systemMemoryStatus.put(MAX_MEMORY, maxMemory);
		systemMemoryStatus.put(TOTAL_MEMORY, totalMemory);
		systemMemoryStatus.put(USED_HEAP_MEMORY, usedHeapMemory);
		systemMemoryStatus.put(USED_NONHEAP_MEMORY, usedNonHeapMemory);
		systemMemoryStatus.put(USED_MEMORY, usedMemory);
		systemMemoryStatus.put(FREE_MEMORY, freeMemory);

		return systemMemoryStatus;
	}
}