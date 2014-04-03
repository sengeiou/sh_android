package com.fav24.dataservices.monitoring.meter;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.monitoring.Meter;

/**
 * Clase para trazar el uso de la memoria
 * de la aplicación.
 * 
 * <pre>
 *  
 * A MemoryUsage object contains four values:
 * 
 * init: represents the initial amount of memory (in bytes) that the Java virtual machine requests from the operating system for memory management during startup. 
 * 		The Java virtual machine may request additional memory from the operating system and may also release memory to the system over time. The value of init may be undefined.
 * used: represents the amount of memory currently used (in bytes).
 * committed: represents the amount of memory (in bytes) that is guaranteed to be available for use by the Java virtual machine. The amount of committed memory may change over
 * 		time (increase or decrease). The Java virtual machine may release memory to the system and committed could be less than init. committed will always be greater than or equal to
 * 		used.
 * max: represents the maximum amount of memory (in bytes) that can be used for memory management. Its value may be undefined. The maximum amount of memory may change over time if
 * 		defined. The amount of used memory will always be less than or equal to max if max is defined. It could be greater than or less than committed. A memory allocation may fail if
 * 		it attempts to increase the used memory such that used > committed even if used <= max would still be true (for example, when the system is low on virtual memory).
 *
 *      Below is a picture showing an example of a memory pool with max < committed.
 *      
 *
 *         +----------------------------------------------+
 *         +////////////////           |                  +
 *         +////////////////           |                  +
 *         +----------------------------------------------+
 * 
 *         |--------|
 *            init
 *         |---------------|
 *                used
 *         |---------------------------|
 *                    max
 *         |----------------------------------------------|
 *                                committed
 * </pre>
 */
public final class MemoryMeter extends Meter {

	public static final String TOTAL_MAX_MEMORY = "TotalMaxMemory";
	public static final String TOTAL_INIT_MEMORY = "TotalInitMemory"; 
	public static final String TOTAL_COMMITTED_MEMORY = "TotalCommitted";
	public static final String TOTAL_USED_MEMORY = "TotalUsedMemory";
	public static final String USED_HEAP_MEMORY = "UsedHeapMemory";
	public static final String USED_NONHEAP_MEMORY = "UsedNonHeapMemory"; 


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
	public NavigableMap<String, Double> getSystemMemoryStatus() {

		NavigableMap<String, Double> systemMemoryStatus = new TreeMap<String, Double>();

		MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean(); 
		MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage(); 
		MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage(); 

		double totalInit = heapMemoryUsage.getInit();
		if (totalInit >= 0) {
			if (nonHeapMemoryUsage.getInit() >= 0) {
				totalInit += nonHeapMemoryUsage.getInit();
			}
			else {
				totalInit = -1;
			}
		}

		double totalMax = heapMemoryUsage.getMax();
		if (totalMax >= 0) {
			if (nonHeapMemoryUsage.getMax() >= 0) {
				totalMax += nonHeapMemoryUsage.getMax();
			}
			else {
				totalMax = -1;
			}
		}

		double totalCommitted = heapMemoryUsage.getCommitted() + nonHeapMemoryUsage.getCommitted();
		double usedHeap = heapMemoryUsage.getUsed(); 
		double usedNonHeap = nonHeapMemoryUsage.getUsed(); 
		double totaUsed = usedHeap + usedNonHeap; 

		systemMemoryStatus.put(TOTAL_INIT_MEMORY, totalInit);
		systemMemoryStatus.put(TOTAL_MAX_MEMORY, totalMax);
		systemMemoryStatus.put(TOTAL_COMMITTED_MEMORY, totalCommitted);
		systemMemoryStatus.put(USED_HEAP_MEMORY, usedHeap);
		systemMemoryStatus.put(USED_NONHEAP_MEMORY, usedNonHeap);
		systemMemoryStatus.put(TOTAL_USED_MEMORY, totaUsed);

		return systemMemoryStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMeterName() {
		return "memory-meter";
	}
}