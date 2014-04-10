package com.fav24.dataservices.monitoring.meter;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.fav24.dataservices.monitoring.Meter;

/**
 * Clase para trazar el trabajo realizado y el thruput del servidor.
 */
public final class WorkloadMeter extends Meter
{
	public static final String MEASURE_START_TIME = "MeasureStartTime"; //Momento de inicio de la medida.
	public static final String INCOMING_REQUESTS_RATE = "RequestsRate"; //Tasa de peticiones entrantes.
	public static final String INCOMING_REQUESTS_RATE_PEAK = "RequestsRatePeak"; //Pico máximo de tasa de peticiones entrantes.
	public static final String TOTAL_INCOMING_REQUESTS = "TotalRequests"; //Número total de peticiones entrantes.
	public static final String TOTAL_INCOMING_REQUESTS_KO = "TotalRequestsKo"; //Número total de peticiones entrantes rechazadas.
	public static final String OPERATION_RATE = "OperationRate"; //Tasa de operaciones procesadas.
	public static final String OPERATION_RATE_PEAK = "OperationRatePeak"; //Pico máximo de tasa de operaciones procesadas.
	public static final String TOTAL_OPERATIONS = "TotalOperations"; //Operaciones procesadas.
	public static final String TOTAL_OPERATIONS_KO = "TotalOperationsKo"; //Operaciones rechazadas.
	public static final String SUBSYSTEM_OPERATION_RATE = "SubsystemOperationRate"; //Tasa de operaciones enviadas al subsistema.
	public static final String SUBSYSTEM_OPERATION_RATE_PEAK = "SubsystemOperationRatePeak"; //Pico máximo de tasa de operaciones enviadas al subsistema.
	public static final String TOTAL_SUBSYSTEM_OPERATIONS = "TotalSubsystemOperations"; //Número total de operaciones enviadas al subsistema.
	public static final String TOTAL_SUBSYSTEM_OPERATIONS_KO = "TotalSubsystemOpertionsKo"; //Número total de operaciones fallidas en el subsistema.


	/**
	 * Hilo de ejecución, para el cálculo del caudal de peticiones entrantes,
	 * y redirigidas al subsistema.
	 */
	public class Throughputs implements Runnable {

		public long startSampleTime;

		public double previousIncoming;
		public double previousOperationIncoming;
		public double previousSubsystemOutcoming;

		public double incomingThroughput;
		public double incomingThroughputPeak;
		public double incomingOperationThroughput;
		public double incomingOperationThroughputPeak;
		public double subsystemOutcomingThroughput;
		public double subsystemOutcomingThroughputPeak;


		/**
		 * Constructor por defecto.
		 */
		public Throughputs() {

			startSampleTime = Long.MIN_VALUE;

			previousIncoming = 0;
			previousOperationIncoming = 0;
			previousSubsystemOutcoming = 0;

			incomingThroughput = 0;
			incomingThroughputPeak = 0;
			incomingOperationThroughput = 0;
			incomingOperationThroughputPeak = 0;
			subsystemOutcomingThroughput = 0;
			subsystemOutcomingThroughputPeak = 0;
		}

		/**
		 * Marca el inicio de una muestra.
		 */
		public void startSampling() {
			startSampleTime = System.currentTimeMillis();
		}

		/**
		 * Inicializa todos los indicadores.
		 */
		public void resetSampling() {
			
			startSampleTime = System.currentTimeMillis();

			previousIncoming = 0;
			previousOperationIncoming = 0;
			previousSubsystemOutcoming = 0;

			incomingThroughput = 0;
			incomingThroughputPeak = 0;
			incomingOperationThroughput = 0;
			incomingOperationThroughputPeak = 0;
			subsystemOutcomingThroughput = 0;
			subsystemOutcomingThroughputPeak = 0;
		}
		
		/**
		 * Recalcula y actualiza los valores del caudal de peticiones y operaciones entrantes y redirigidas.
		 * 
		 * @param currentIncoming Número total de peticiones realizadas al servidor.
		 * @param currentOperationIncoming Número total de operaciones a resolver por el servidor.
		 * @param currentSubsystemOutcoming Número total de operaciones enviadas al subsistema.
		 */
		public void updateThroughputs(long currentIncoming, long currentOperationIncoming, long currentSubsystemOutcoming) {

			long endSampleTime = System.currentTimeMillis();

			incomingThroughput = ((currentIncoming - previousIncoming) * 1000) / (endSampleTime - startSampleTime);
			incomingOperationThroughput = ((currentOperationIncoming - previousOperationIncoming) * 1000) / (endSampleTime - startSampleTime);
			subsystemOutcomingThroughput = ((currentSubsystemOutcoming - previousSubsystemOutcoming) * 1000) / (endSampleTime - startSampleTime);

			previousIncoming = currentIncoming;
			previousOperationIncoming = currentOperationIncoming;
			previousSubsystemOutcoming = currentSubsystemOutcoming;

			if (incomingThroughputPeak < incomingThroughput) {
				incomingThroughputPeak = incomingThroughput;
			}

			if (incomingOperationThroughputPeak < incomingOperationThroughput) {
				incomingOperationThroughputPeak = incomingOperationThroughput;
			}

			if (subsystemOutcomingThroughputPeak < subsystemOutcomingThroughput) {
				subsystemOutcomingThroughputPeak = subsystemOutcomingThroughput;
			}
		}

		/**
		 * Retorna el caudal de peticiones entrantes.
		 * 
		 * @return el caudal de peticiones entrantes.
		 */
		public double getIncomingRequestsRate() {

			return incomingThroughput;
		}

		/**
		 * Retorna el pico máximo de caudal de peticiones entrantes.
		 * 
		 * @return el pico máximo de caudal de peticiones entrantes.
		 */
		public double getIncomingRequestsRatePeak() {

			return incomingThroughputPeak;
		}

		/**
		 * Retorna el caudal de operaciones entrantes.
		 * 
		 * @return el caudal de operaciones entrantes.
		 */
		public double getIncomingOperationRate() {

			return incomingOperationThroughput;
		}

		/**
		 * Retorna el pico máximo de caudal de operaciones entrantes.
		 * 
		 * @return el pico máximo de caudal de operaciones entrantes.
		 */
		public double getIncomingOperationRatePeak() {

			return incomingOperationThroughputPeak;
		}

		/**
		 * Retorna el caudal de peticiones enviadas al subsistema.
		 * 
		 * @return el caudal de peticiones enviadas al subsistema.
		 */
		public double getSubsystemOutcomingRate() {

			return subsystemOutcomingThroughput;
		}

		/**
		 * Retorna el pico máximo de caudal de peticiones enviadas al subsistema.
		 * 
		 * @return el pico máximo de caudal de peticiones enviadas al subsistema.
		 */
		public double getSubsystemOutcomingRatePeak() {

			return subsystemOutcomingThroughputPeak;
		}

		/**
		 * {@inheritDoc}
		 */
		public void run() {

			startSampling();

			while (!measureThread.isInterrupted()) {

				try {
					Thread.sleep(1000); 
				}
				catch (InterruptedException e) {
					break; 
				}

				updateThroughputs(totalIncommingRequests, totalIncommingOperations, totalSubsystemOutcommingOperations);
				startSampling();
			}
		}
	}

	private final Throughputs throughputs;

	private long measureStartTime;
	private long totalIncommingRequests;
	private long totalIncommingRequestsErrors;
	private long totalIncommingOperations;
	private long totalIncommingOperationsErrors;
	private long totalSubsystemOutcommingOperations;
	private long totalSubsystemOutcommingOperationsErrors;
	private Thread measureThread;


	/**
	 * Constructor por defecto.
	 */
	public WorkloadMeter() {

		this.throughputs = new Throughputs();
		this.measureThread = new Thread(this.throughputs, "Thread workload meter");
		this.measureThread.setDaemon(true);
		
		newMeasurePeriod();
		
		this.measureThread.start();
	}

	/**
	 * Retorna el momento de inicio de las medidas para el periodo actual.
	 * 
	 * Nota: este valor está expresado en milisegundos desde epoch.
	 * 
	 * @return el momento de inicio de las medidas para el periodo actual.
	 */
	public long getMeasureStartTime() {

		return measureStartTime;	
	}

	/**
	 * Establece un nuevo periodo de medidas.
	 * 
	 * @return el momento de inicio del nuevo periodo en milisegundos desde epoch.
	 */
	public long newMeasurePeriod() {

		measureStartTime = System.currentTimeMillis();
		
		totalIncommingRequests = 0;
		totalIncommingRequestsErrors = 0;
		totalIncommingOperations = 0;
		totalIncommingOperationsErrors = 0;
		totalSubsystemOutcommingOperations = 0;
		totalSubsystemOutcommingOperationsErrors = 0;
		
		throughputs.resetSampling();
		
		return measureStartTime;
	}

	/**
	 * Retorna el número total de peticiones recibidas.
	 * 
	 * @return el número total de peticiones recibidas.
	 */
	public long getTotalIncommingRequests() {

		return totalIncommingRequests;
	}

	/**
	 * Incrementa en 1 el número total de peticiones recibidas.
	 */
	public void incTotalIncommingRequests() {

		this.totalIncommingRequests++;
	}

	/**
	 * Retorna el número total de peticiones recibidas con errores.
	 * 
	 * @return el número total de peticiones recibidas con errores.
	 */
	public long getTotalIncommingRequestsKo() {

		return totalIncommingRequestsErrors;
	}

	/**
	 * Incrementa en 1 el número total de peticiones con errores.
	 */
	public void incTotalIncommingRequestsErrors() {

		this.totalIncommingRequestsErrors++;
	}

	/**
	 * Retorna el número total de operaciones recibidas.
	 * 
	 * @return el número total de operaciones recibidas.
	 */
	public long getTotalIncommingOperations() {

		return totalIncommingOperations;
	}

	/**
	 * Incrementa en 1 el número total de operaciones recibidas.
	 */
	public void incTotalIncommingOperations() {

		this.totalIncommingOperations++;
	}

	/**
	 * Retorna el número total de operaciones recibidas con errores.
	 * 
	 * @return el número total de operaciones recibidas con errores.
	 */
	public long getTotalIncommingOperationsKo() {

		return totalIncommingOperationsErrors;
	}

	/**
	 * Incrementa en 1 el número total de operaciones con errores.
	 */
	public void incTotalIncommingOperationsErrors() {

		this.totalIncommingOperationsErrors++;
	}

	/**
	 * Retorna el número total de peticiones enviadas al subsistema.
	 * 
	 * @return el número total de peticiones enviadas al subsistema.
	 */
	public long getTotalSubsystemOutcommingOperations() {

		return totalSubsystemOutcommingOperations;
	}

	/**
	 * Incrementa en 1 el número total de operaciones enviadas al subsistema.
	 */
	public void incTotalSubsystemOutcommingOperations() {

		this.totalSubsystemOutcommingOperations++;
	}

	/**
	 * Retorna el número total de operaciones erroneas enviadas al subsistema.
	 * 
	 * @return el número total de operaciones erroneas enviadas al subsistema.
	 */
	public long getTotalSubsystemOutcommingOperationsKo() {

		return totalSubsystemOutcommingOperationsErrors;
	}

	/**
	 * Incrementa en 1 el número total de operaciones erroneas enviadas al subsistema.
	 */
	public void incTotalSubsystemOutcommingOpertionsErrors() {

		this.totalSubsystemOutcommingOperationsErrors ++;
	}

	/**
	 * Retorna la información del trabajo realizado por el servidor.
	 *  
	 * @return la información del trabajo realizado por el servidor.
	 */
	public NavigableMap<String, Double> getSystemWorkload() {

		NavigableMap<String, Double> systemWorkload = new TreeMap<String, Double>();

		systemWorkload.put(MEASURE_START_TIME, Double.valueOf(getMeasureStartTime()));
		systemWorkload.put(INCOMING_REQUESTS_RATE, throughputs.getIncomingRequestsRate());
		systemWorkload.put(INCOMING_REQUESTS_RATE_PEAK, throughputs.getIncomingRequestsRatePeak());
		systemWorkload.put(TOTAL_INCOMING_REQUESTS, Double.valueOf(getTotalIncommingRequests()));
		systemWorkload.put(TOTAL_INCOMING_REQUESTS_KO, Double.valueOf(getTotalIncommingRequestsKo()));

		systemWorkload.put(OPERATION_RATE, throughputs.getIncomingOperationRate());
		systemWorkload.put(OPERATION_RATE_PEAK, throughputs.getIncomingOperationRatePeak());
		systemWorkload.put(TOTAL_OPERATIONS, Double.valueOf(getTotalIncommingOperations()));
		systemWorkload.put(TOTAL_OPERATIONS_KO, Double.valueOf(getTotalIncommingOperationsKo()));

		systemWorkload.put(SUBSYSTEM_OPERATION_RATE, throughputs.getSubsystemOutcomingRate());
		systemWorkload.put(SUBSYSTEM_OPERATION_RATE_PEAK, throughputs.getSubsystemOutcomingRatePeak());
		systemWorkload.put(TOTAL_SUBSYSTEM_OPERATIONS, Double.valueOf(getTotalSubsystemOutcommingOperations()));
		systemWorkload.put(TOTAL_SUBSYSTEM_OPERATIONS_KO, Double.valueOf(getTotalSubsystemOutcommingOperationsKo()));

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