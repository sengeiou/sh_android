package com.fav24.dataservices.monitoring.meter;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.monitoring.Meter;

/**
 * Clase para trazar el trabajo realizado y el thruput del servidor.
 */
public final class WorkloadMeter extends Meter
{
	public static final String THROUGHPUT_PEAK = "ThroughputPeak"; //Pico máximo de peticiones entrantes por segundo.
	public static final String THROUGHPUT = "Throughput"; //Peticiones entrantes por segundo.
	public static final String TOTAL_ACCEPTED = "TotalAccepted"; //Número total de peticiones servidas con éxito.
	public static final String TOTAL_REVOQUED = "TotalRevoqued"; //Número total de peticiones rechazadas.
	public static final String SUBSYSTEM_THROUGHPUT_PEAK = "PeakSubsystemThroughput"; //Pico máximo de peticiones realizadas al subsistema por segundo.
	public static final String SUBSYSTEM_THROUGHPUT = "SubsystemThroughput"; //Número de threads inciados desde el arranque de la aplicación.
	public static final String TOTAL_SUBSYSTEM_REQUESTS_OK = "TotalSubsystemRequestOk"; //Número total de peticiones realizadas al subsistema con éxito.
	public static final String TOTAL_SUBSYSTEM_REQUESTS_KO = "TotalSubsystemRequestKo"; //Número total de peticiones rechazadas por el subsistema.


	public class Throughputs implements Runnable {

		public long startSampleTime;
		public long endSampleTime;

		public double previousIncoming;
		public double previousSubsystemOutcoming;

		public double incomingThroughput;
		public double incomingThroughputPeak;
		public double subsystemoutcomingThroughput;
		public double subsystemoutcomingThroughputPeak;


		public Throughputs() {

			startSampleTime = Long.MIN_VALUE;
			endSampleTime = Long.MIN_VALUE;

			previousIncoming = 0;
			previousSubsystemOutcoming = 0;

			incomingThroughput = 0;
			incomingThroughputPeak = 0;
			subsystemoutcomingThroughput = 0;
			subsystemoutcomingThroughputPeak = 0;
		}

		public void startSampling() {
			startSampleTime = System.currentTimeMillis();
		}

		public void endSampling() {
			endSampleTime = System.currentTimeMillis();
		}

		public void updateThroughputs(long currentIncoming, long currentSubsystemOutcoming) {

			incomingThroughput = (currentIncoming - previousIncoming) / (endSampleTime - startSampleTime);
			subsystemoutcomingThroughput = (currentSubsystemOutcoming - previousSubsystemOutcoming) / (endSampleTime - startSampleTime);

			previousIncoming = currentIncoming;
			previousSubsystemOutcoming = currentSubsystemOutcoming;

			if (incomingThroughputPeak < incomingThroughput) {
				incomingThroughputPeak = incomingThroughput;
			}

			if (subsystemoutcomingThroughputPeak < subsystemoutcomingThroughput) {
				subsystemoutcomingThroughputPeak = subsystemoutcomingThroughput;
			}
		}

		public double getIncomingThroughput() {

			return incomingThroughput;
		}

		public double getIncomingThroughputPeak() {

			return incomingThroughputPeak;
		}

		public double getSubsystemoutcomingThroughput() {

			return subsystemoutcomingThroughput;
		}

		public double getSubsystemoutcomingThroughputPeak() {

			return subsystemoutcomingThroughputPeak;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {

			startSampling();

			while (!measureThread.isInterrupted()) {

				endSampling();
				updateThroughputs(totalIncomming, totalSubsystemOutcomming);
				startSampling();

				try {
					Thread.sleep(1000); 
				}
				catch (InterruptedException e) {
					break; 
				}
			}
		}
	}

	private final Throughputs throughputs;

	private long totalIncomming;
	private long totalIncommingRevoqued;
	private long totalSubsystemOutcomming;
	private long totalSubsystemOutcommingErrors;
	private Thread measureThread;


	/**
	 * Constructor por defecto.
	 */
	public WorkloadMeter() {

		this.throughputs = new Throughputs();
		this.measureThread = new Thread(this.throughputs, "Thread workload meter");
		this.measureThread.setDaemon(true);
		this.measureThread.start();
	}

	/**
	 * Retorna el número total de peticiones recibidas.
	 * 
	 * @return el número total de peticiones recibidas.
	 */
	public long getTotalIncomming() {

		return totalIncomming;
	}
	
	/**
	 * Asigna el número total de peticiones recibidas.
	 * 
	 * @param totalIncomming Número total de peticiones recibidas a asignar.
	 */
	public void setTotalIncomming(long totalIncomming) {
		
		this.totalIncomming = totalIncomming;
	}

	/**
	 * Retorna el número total de peticiones recibidas rechazadas.
	 * 
	 * @return el número total de peticiones recibidas rechazadas.
	 */
	public long getTotalIncommingRevoqued() {

		return totalIncommingRevoqued;
	}

	/**
	 * Asigna el número total de peticiones rechazadas.
	 * 
	 * @param totalIncommingRevoqued Número total de peticiones rechazadas a asignar.
	 */
	public void setTotalIncommingRevoqued(long totalIncommingRevoqued) {
		
		this.totalIncommingRevoqued = totalIncommingRevoqued;
	}
	
	/**
	 * Retorna el número total de peticiones enviadas al subsistema.
	 * 
	 * @return el número total de peticiones enviadas al subsistema.
	 */
	public long getTotalSubsystemOutcomming() {

		return totalSubsystemOutcomming;
	}
	
	/**
	 * Asigna el número total de peticiones enviadas al subsistema.
	 * 
	 * @param totalSubsystemOutcomming El número total de peticiones enviadas al subsistema a asginar.
	 */
	public void setTotalSubsystemOutcomming(long totalSubsystemOutcomming) {
		
		this.totalSubsystemOutcomming = totalSubsystemOutcomming;
	}

	/**
	 * Retorna el número total de peticiones erroneas al subsistema.
	 * 
	 * @return el número total de peticiones erroneas al subsistema.
	 */
	public long getTotalSubsystemOutcommingErrors() {

		return totalSubsystemOutcommingErrors;
	}
	
	/**
	 * Asigna el número total de peticiones erroneas al subsistema.
	 * 
	 * @param totalSubsystemOutcommingErrors El número total de peticiones erroneas al subsistema a asignar.
	 */
	public void setTotalSubsystemOutcommingErrors(long totalSubsystemOutcommingErrors) {
		
		this.totalSubsystemOutcommingErrors = totalSubsystemOutcommingErrors;
	}

	/**
	 * Retorna la información del trabajo realizado por el servidor.
	 *  
	 * @return la información del trabajo realizado por el servidor.
	 */
	public NavigableMap<String, Double> getSystemWorkload() {

		NavigableMap<String, Double> systemWorkload = new TreeMap<String, Double>();

		systemWorkload.put(THROUGHPUT_PEAK, throughputs.getIncomingThroughputPeak());
		systemWorkload.put(THROUGHPUT, throughputs.getIncomingThroughput());
		long totalIncommingRevoqued = getTotalIncommingRevoqued();
		systemWorkload.put(TOTAL_ACCEPTED, Double.valueOf(getTotalIncomming() - totalIncommingRevoqued));
		systemWorkload.put(TOTAL_REVOQUED, Double.valueOf(totalIncommingRevoqued));

		systemWorkload.put(SUBSYSTEM_THROUGHPUT_PEAK, throughputs.getSubsystemoutcomingThroughputPeak());
		systemWorkload.put(SUBSYSTEM_THROUGHPUT, throughputs.getSubsystemoutcomingThroughput());
		long totalSubsystemOutcommingErrors = getTotalSubsystemOutcommingErrors();
		systemWorkload.put(TOTAL_SUBSYSTEM_REQUESTS_OK, Double.valueOf(getTotalSubsystemOutcomming() - totalSubsystemOutcommingErrors));
		systemWorkload.put(TOTAL_SUBSYSTEM_REQUESTS_KO, Double.valueOf(totalSubsystemOutcommingErrors));

		return systemWorkload;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getMeterName() {
		return "workload-meter";
	}
}