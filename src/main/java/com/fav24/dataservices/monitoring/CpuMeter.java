package com.fav24.dataservices.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Clase para trazar tiempos de uso de CPU de los hilos
 * de la aplicación a cada segundo.
 */
public final class CpuMeter extends Meter implements Runnable
{
	public static final String PEAK_THREAD_COUNT = "PeakThreadCount"; //Pico máximo de threads activos.
	public static final String TOTAL_STARTED_THREAD_COUNT = "TotalStartedThreadCount"; //Número de threads inciados desde el arranque de la aplicación.
	public static final String TOTAL_DEAMON_THREAD_COUNT = "DaemonThreadCount"; //Número de threads de tipo demonio activos.
	public static final String NUMBER_OF_THREADS = "NumberOfThreads"; //Número de threads activos.
	public static final String SYSTEM_CPU_LOAD = "SystemCPULoad"; //Carga de la CPU por procesos del sistema.
	public static final String APPLICATION_CPU_LOAD = "ApplicationCPULoad"; //Carga de la CPU por procesos de la aplicación.
	public static final String CPU_LOAD = "CPULoad"; //Carga general de la CPU.

	public class Times {

		public long id;
		public long startSampleTime;
		public long endSampleTime;
		public long previousCpuTime;
		public long previousApplicationTime;
		public long currentCpuTime;
		public long currentApplicationTime;

		public long getTotalUserTime() {

			final Collection<Times> hist = history.values();
			long time = 0L;

			for (Times times : hist) {
				time += times.currentApplicationTime - times.previousApplicationTime;
			}

			return time;
		}
	}

	private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

	private final long interval;
	private final long threadId;
	private final HashMap<Long, Times> history = new HashMap<Long, Times>();

	private long totalCpuTime;
	private long totalApplicationTime;
	private long totalSystemTime;
	private long measureTime;
	private Thread measureThread;


	/**
	 * Constructor por defecto.
	 */
	public CpuMeter() {

		this(300L);
	}

	/** 
	 * Constructor con parámetro.
	 * 
	 * @param interval Intervalo de tiempo entre medidas en milisegundos.
	 */
	public CpuMeter(final long interval) {

		this.interval = interval;
		this.measureThread = new Thread(this, "Thread time monitor");
		this.threadId = this.measureThread.getId();
		this.measureThread.setDaemon(true);
		
		this.measureThread.start();
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {

		while (!measureThread.isInterrupted()) {

			update();

			try {
				Thread.sleep(interval); 
			}
			catch (InterruptedException e) {
				break; 
			}
		}
	}

	/**
	 * Actualiza las medidas de uso de los hilos.
	 */
	private void update() {

		final long[] ids = threadMXBean.getAllThreadIds();

		// Se eliminan del histórico los threads desaparecidos.
		Long []threadIds = new Long[history.size()]; 
		history.keySet().toArray(threadIds);
		
		for (long threadId : threadIds) {
			
			boolean found = false;
			for (long id : ids) {
				if (threadId == id) {
					found = true;
					break;
				}
			}
			
			if (!found) {
				history.remove(threadId);
			}
		}
		
		long totalCpuTime = 0L;
		long totalApplicationTime = 0L;

		long measureStartTime = Long.MAX_VALUE;
		long measureEndTime = 0;

		for (long id : ids) {

			if (id == threadId) {
				continue;   // Se excluye este hilo.
			}

			final long cpu = threadMXBean.getThreadCpuTime(id);
			final long application = threadMXBean.getThreadUserTime(id);

			if (cpu == -1 || application == -1) {

				history.remove(id);
				continue;   // El hilo ya ha muerto.
			}

			Times times = history.get(id);

			if (times == null) {

				times = new Times();
				times.id = id;
				times.startSampleTime = System.nanoTime();
				times.endSampleTime = System.nanoTime();
				times.previousCpuTime = cpu;
				times.previousApplicationTime = application;
				times.currentCpuTime = cpu;
				times.currentApplicationTime = application;
				history.put(id, times);
			}
			else {

				times.startSampleTime = times.endSampleTime;
				times.endSampleTime = System.nanoTime();
				times.previousCpuTime = times.currentCpuTime;
				times.previousApplicationTime = times.currentApplicationTime;
				times.currentCpuTime = cpu;
				times.currentApplicationTime = application;
			}

			if (measureStartTime > times.startSampleTime) {
				measureStartTime = times.startSampleTime;
			}

			if (measureEndTime < times.endSampleTime) {
				measureEndTime = times.endSampleTime;
			}

			totalCpuTime += times.currentCpuTime - times.previousCpuTime;
			totalApplicationTime += times.currentApplicationTime - times.previousApplicationTime;
		}

		this.totalCpuTime = totalCpuTime;
		this.totalApplicationTime = totalApplicationTime;
		this.totalSystemTime = totalCpuTime - totalApplicationTime;
		this.measureTime = measureEndTime - measureStartTime;
	}

	/**
	 * Retorna el tiempo total de CPU en nanosegundos.
	 * 
	 * @return el tiempo total de CPU en nanosegundos.
	 */
	public long getTotalCpuTime() {

		return totalCpuTime;
	}

	/**
	 * Retorna el tiempo total de usuario en nanosegundos.
	 * 
	 * @return el tiempo total de usuario en nanosegundos.
	 */
	public long getTotalApplicationTime() {

		return totalApplicationTime;
	}

	/**
	 * Retorna el tiempo total de systema en nanosegundos.
	 * 
	 * @return el tiempo total de systema en nanosegundos.
	 */
	public long getTotalSystemTime() {

		return totalSystemTime;
	}

	/**
	 * Retorna la información de la actividad de la CPU.
	 *  
	 * @return la información de la actividad de la CPU.
	 */
	public NavigableMap<String, Double> getSystemCpuActivity() {

		NavigableMap<String, Double> systemCpuActivity = new TreeMap<String, Double>();

		systemCpuActivity.put(PEAK_THREAD_COUNT, Double.valueOf(threadMXBean.getPeakThreadCount()));
		systemCpuActivity.put(TOTAL_STARTED_THREAD_COUNT, Double.valueOf(threadMXBean.getTotalStartedThreadCount()));
		systemCpuActivity.put(TOTAL_DEAMON_THREAD_COUNT, Double.valueOf(threadMXBean.getDaemonThreadCount()));
		systemCpuActivity.put(NUMBER_OF_THREADS, Double.valueOf(history.size()));

		double systemCpuLoad = measureTime == 0 ? 0 : ((double)(totalSystemTime*100))/measureTime ;
		double applicationCpuLoad = measureTime == 0 ? 0 : ((double)(totalApplicationTime*100))/measureTime;
		double totalCpuLoad = measureTime == 0 ? 0 : ((double)(totalCpuTime*100))/measureTime;

		systemCpuActivity.put(SYSTEM_CPU_LOAD, systemCpuLoad);
		systemCpuActivity.put(APPLICATION_CPU_LOAD, applicationCpuLoad);
		systemCpuActivity.put(CPU_LOAD, totalCpuLoad);

		return systemCpuActivity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMeterName() {
		return "cpu-meter";
	}
}