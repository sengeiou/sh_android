package com.fav24.dataservices.monitoring;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Clase para trazar tiempos de uso de CPU de los hilos
 * de la aplicación a cada segundo.
 */
public final class CpuMeter extends Thread
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
		public long startCpuTime;
		public long startApplicationTime;
		public long endCpuTime;
		public long endApplicationTime;

		public long getTotalUserTime() {

			final Collection<Times> hist = history.values();
			long time = 0L;

			for (Times times : hist) {
				time += times.endApplicationTime - times.startApplicationTime;
			}

			return time;
		}
	}

	private static final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private static final RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
	
	
	private final long interval;
	private final long threadId;
	private final HashMap<Long, Times> history = new HashMap<Long, Times>();

	private long numSamples;
	private final long maxSamples;
	private long totalCpuTime;
	private long totalApplicationTime;
	private long totalSystemTime;
	private long upTime;


	/**
	 * Constructor por defecto.
	 */
	public CpuMeter() {

		this(100L);
	}

	/** 
	 * Constructor con parámetro.
	 * 
	 * @param interval Intervalo de tiempo entre medidas en milisegundos.
	 */
	public CpuMeter(final long interval) {

		super("Thread time monitor");

		this.interval = interval;
		this.maxSamples = 1000L / interval; // Para saber el número de medidas a tener en cuenta en 1 segundo.

		threadId = getId();

		setDaemon(true);
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {

		while (!isInterrupted()) {

			if (this.numSamples >= maxSamples) {
				calculateTimes();
				history.clear();	
				numSamples = 0;
			}

			update();

			this.numSamples++;

			try {
				sleep(interval); 
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
				times.startCpuTime = cpu;
				times.startApplicationTime = application;
				times.endCpuTime = cpu;
				times.endApplicationTime = application;
				history.put(id, times);
			}
			else {

				times.endCpuTime = cpu;
				times.endApplicationTime = application;
			}
		}
	}


	/**
	 * Calcula los tiempos de CPU.
	 */
	private void calculateTimes() {

		final Collection<Times> hist = history.values();

		if (hist.size() > 0) {

			long totalCpuTime = 0L;
			long totalApplicationTime = 0L;

			for (Times times : hist) {
				totalCpuTime += times.endCpuTime - times.startCpuTime;
				totalApplicationTime += times.endApplicationTime - times.startApplicationTime;
			}

			this.totalCpuTime = totalCpuTime;
			this.totalApplicationTime = totalApplicationTime;
			this.totalSystemTime = totalCpuTime - totalApplicationTime;
			this.upTime = runtimeMXBean.getUptime() * 1000000L;
		}
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

		double systemCpuLoad = upTime == 0 ? 0 : (totalSystemTime/upTime) * 100;
		double applicationCpuLoad = upTime == 0 ? 0 : (totalApplicationTime/upTime) * 100;
		double totalCpuLoad = upTime == 0 ? 0 : (totalCpuTime/upTime) * 100;

		systemCpuActivity.put(SYSTEM_CPU_LOAD, systemCpuLoad);
		systemCpuActivity.put(APPLICATION_CPU_LOAD, applicationCpuLoad);
		systemCpuActivity.put(CPU_LOAD, totalCpuLoad);

		return systemCpuActivity;
	}
}