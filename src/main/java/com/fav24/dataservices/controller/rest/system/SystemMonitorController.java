package com.fav24.dataservices.controller.rest.system;


import java.util.AbstractList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.dto.system.SystemMonitorInfoDto;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.monitoring.Meter;
import com.fav24.dataservices.monitoring.MonitorSample;
import com.fav24.dataservices.monitoring.meter.CacheMeter;
import com.fav24.dataservices.monitoring.meter.CpuMeter;
import com.fav24.dataservices.monitoring.meter.MemoryMeter;
import com.fav24.dataservices.monitoring.meter.WorkloadMeter;
import com.fav24.dataservices.service.system.SystemService;

/**
 * Controla las peticiones de entrada a los servicios de gestión del sistema.
 */
@Scope("singleton")
@Controller
@RequestMapping("/system")
public class SystemMonitorController extends BaseRestController {

	private static final Logger logger = LoggerFactory.getLogger(SystemMonitorController.class);

	public static final Long DEFAULT_PERIOD = 1L; // Resolución por defecto de la información en segundos.
	public static final Long DEFAULT_TIME_RANGE = 300L; // Rango de la información en segundos. ( los últimos 5 minutos.)

	public static final String MEMORY_MONITOR = "MemoryMonitor";
	public static final String CPU_MONITOR = "CPUMonitor";
	public static final String WORKLOAD_MONITOR = "WorkloadMonitor";
	public static final String CACHE_MONITOR = "CacheMonitor";
	public static final String STORAGE_MONITOR = "StorageMonitor";

	private SystemService systemService;
	private CpuMeter cpuMeter;


	/**
	 * Constructor por defecto.
	 */
	public SystemMonitorController() {

	}

	/**
	 * Asigna la instancia del servicio de sistema.
	 * 
	 * @param systemService Instancia a asignar.
	 */
	@Autowired  
	public void setSystemService(SystemService systemService) {  
		this.systemService = systemService;
		this.cpuMeter = systemService.getCpuMeter();
	} 

	/**
	 * Procesa una petición de la fecha y hora del sistema en milisegundos desde epoch.
	 * 
	 * @return la fecha y hora del sistema en milisegundos desde epoch.
	 */
	@RequestMapping(value = "/time", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Long getTime() {

		return System.currentTimeMillis();
	}

	/**
	 * Retorna una cadena estática para permitir chequear el servicio, al mínimo coste posible.
	 * 
	 * @return una cadena estática para permitir chequear el servicio, al mínimo coste posible.
	 */
	@RequestMapping(value = "/ping", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String getPing() {

		return "pong";
	}

	/**
	 * Procesa una petición de información del estado de la memoria del sistema.
	 * 
	 * @param parameters Mapa de parámetros del que se obtienen el nombre, periodo y el corte temporal.
	 * 
	 * Nota: en caso de no indicar rango temporal o periodo, se informa únicamente con la última muestra recogida por el monitor.
	 *  
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/memory", method = { RequestMethod.POST })
	public @ResponseBody SystemMonitorInfoDto getMemory(@RequestBody final Map<String, Object> parameters) {

		long threadId = Thread.currentThread().getId();

		cpuMeter.excludeThread(threadId);

		Number offset = (Number) parameters.get("offset");
		Number period = (Number) parameters.get("period");
		Number timeRange = (Number) parameters.get("timeRange");

		SystemMonitorInfoDto systemMonitorInfo = new SystemMonitorInfoDto();

		systemMonitorInfo.setName(MEMORY_MONITOR);
		if (offset != null) {
			systemMonitorInfo.setOffset(offset.longValue());
		}
		if (period != null) {
			systemMonitorInfo.setPeriod(period.longValue());
		}
		if (timeRange != null) {
			systemMonitorInfo.setTimeRange(timeRange.longValue());
		}

		if (systemMonitorInfo.getPeriod() == null || systemMonitorInfo.getTimeRange() == null) {

			MonitorSample memoryMonitorSample = systemService.getSystemMemoryStatus();

			if (memoryMonitorSample != null) {

				Object[][] totalInitMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_INIT_MEMORY)}};
				Object[][] totalMaxMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_MAX_MEMORY)}};
				Object[][] totalCommittedMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_COMMITTED_MEMORY)}};
				Object[][] totalUsedMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_USED_MEMORY)}};
				Object[][] UsedHeapMemoryData = {{memoryMonitorSample.getData(MemoryMeter.USED_HEAP_MEMORY)}};
				Object[][] UsedNonHeapMemoryData = {{memoryMonitorSample.getData(MemoryMeter.USED_NONHEAP_MEMORY)}};

				systemMonitorInfo.getData().put(MemoryMeter.TOTAL_INIT_MEMORY, totalInitMemoryData);
				systemMonitorInfo.getData().put(MemoryMeter.TOTAL_MAX_MEMORY, totalMaxMemoryData);
				systemMonitorInfo.getData().put(MemoryMeter.TOTAL_COMMITTED_MEMORY, totalCommittedMemoryData);
				systemMonitorInfo.getData().put(MemoryMeter.TOTAL_USED_MEMORY, totalUsedMemoryData);
				systemMonitorInfo.getData().put(MemoryMeter.USED_HEAP_MEMORY, UsedHeapMemoryData);
				systemMonitorInfo.getData().put(MemoryMeter.USED_NONHEAP_MEMORY, UsedNonHeapMemoryData);
			}
			else {
				systemMonitorInfo.setData(null);
			}

		}
		else {

			try {
				AbstractList<MonitorSample> systemMemoryStatus = systemService.getSystemMemoryStatus(systemMonitorInfo.getOffset(), systemMonitorInfo.getTimeRange(), systemMonitorInfo.getPeriod());

				if (systemMemoryStatus != null) {

					Object[][] totalInitMemoryData = new Object[systemMemoryStatus.size()][2];
					Object[][] totalMaxMemoryData = new Object[systemMemoryStatus.size()][2];
					Object[][] totalCommittedMemoryData = new Object[systemMemoryStatus.size()][2];
					Object[][] totalUsedMemoryData = new Object[systemMemoryStatus.size()][2];
					Object[][] UsedHeapMemoryData = new Object[systemMemoryStatus.size()][2];
					Object[][] UsedNonHeapMemoryData = new Object[systemMemoryStatus.size()][2];

					int i = 0;
					for (MonitorSample monitorSample : systemMemoryStatus) {

						totalInitMemoryData[i][0] = monitorSample.getTime();
						totalInitMemoryData[i][1] = monitorSample.getData(MemoryMeter.TOTAL_INIT_MEMORY);
						totalMaxMemoryData[i][0] = monitorSample.getTime();
						totalMaxMemoryData[i][1] = monitorSample.getData(MemoryMeter.TOTAL_MAX_MEMORY);
						totalCommittedMemoryData[i][0] = monitorSample.getTime();
						totalCommittedMemoryData[i][1] = monitorSample.getData(MemoryMeter.TOTAL_COMMITTED_MEMORY);
						totalUsedMemoryData[i][0] = monitorSample.getTime();
						totalUsedMemoryData[i][1] = monitorSample.getData(MemoryMeter.TOTAL_USED_MEMORY);
						UsedHeapMemoryData[i][0] = monitorSample.getTime();
						UsedHeapMemoryData[i][1] = monitorSample.getData(MemoryMeter.USED_HEAP_MEMORY);
						UsedNonHeapMemoryData[i][0] = monitorSample.getTime();
						UsedNonHeapMemoryData[i][1] = monitorSample.getData(MemoryMeter.USED_NONHEAP_MEMORY);

						i++;
					}

					systemMonitorInfo.getData().put(MemoryMeter.TOTAL_INIT_MEMORY, totalInitMemoryData);
					systemMonitorInfo.getData().put(MemoryMeter.TOTAL_MAX_MEMORY, totalMaxMemoryData);
					systemMonitorInfo.getData().put(MemoryMeter.TOTAL_COMMITTED_MEMORY, totalCommittedMemoryData);
					systemMonitorInfo.getData().put(MemoryMeter.TOTAL_USED_MEMORY, totalUsedMemoryData);
					systemMonitorInfo.getData().put(MemoryMeter.USED_HEAP_MEMORY, UsedHeapMemoryData);
					systemMonitorInfo.getData().put(MemoryMeter.USED_NONHEAP_MEMORY, UsedNonHeapMemoryData);
				}
				else {
					systemMonitorInfo.setData(null);
				}

			} catch (ServerException e) {

				e.log(logger, false);
			}
		}

		cpuMeter.includeThread(threadId);

		return systemMonitorInfo;
	}

	/**
	 * Procesa una petición de información de la actividad de la CPU del sistema.
	 * 
	 * @param parameters Mapa de parámetros del que se obtienen el nombre, periodo y el corte temporal.
	 * 
	 * Nota: en caso de no indicar rango temporal o periodo, se informa únicamente con la última muestra recogida por el monitor.
	 *  
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/cpu", method = { RequestMethod.POST })
	public @ResponseBody SystemMonitorInfoDto getCPU(@RequestBody final Map<String, Object> parameters) {

		long threadId = Thread.currentThread().getId();

		cpuMeter.excludeThread(threadId);

		Number offset = (Number) parameters.get("offset");
		Number period = (Number) parameters.get("period");
		Number timeRange = (Number) parameters.get("timeRange");

		SystemMonitorInfoDto systemMonitorInfo = new SystemMonitorInfoDto();

		systemMonitorInfo.setName(CPU_MONITOR);
		if (offset != null) {
			systemMonitorInfo.setOffset(offset.longValue());
		}
		if (period != null) {
			systemMonitorInfo.setPeriod(period.longValue());
		}
		if (timeRange != null) {
			systemMonitorInfo.setTimeRange(timeRange.longValue());
		}

		if (systemMonitorInfo.getPeriod() == null || systemMonitorInfo.getTimeRange() == null) {

			MonitorSample cpuMonitorSample = systemService.getSystemCpuActivity();

			if (cpuMonitorSample != null) {

				Object[][] peakThreadCountData = {{cpuMonitorSample.getData(CpuMeter.PEAK_THREAD_COUNT)}};
				Object[][] totalStartedThreadCountData = {{cpuMonitorSample.getData(CpuMeter.TOTAL_STARTED_THREAD_COUNT)}};
				Object[][] daemonThreadCountData = {{cpuMonitorSample.getData(CpuMeter.TOTAL_DEAMON_THREAD_COUNT)}};
				Object[][] numberOfThreadsData = {{cpuMonitorSample.getData(CpuMeter.NUMBER_OF_THREADS)}};
				Object[][] totalCpuLoadData = {{cpuMonitorSample.getData(CpuMeter.CPU_LOAD)}};
				Object[][] applicationCpuLoadData = {{cpuMonitorSample.getData(CpuMeter.APPLICATION_CPU_LOAD)}};
				Object[][] systemCpuLoadData = {{cpuMonitorSample.getData(CpuMeter.SYSTEM_CPU_LOAD)}};

				systemMonitorInfo.getData().put(CpuMeter.PEAK_THREAD_COUNT, peakThreadCountData);
				systemMonitorInfo.getData().put(CpuMeter.TOTAL_STARTED_THREAD_COUNT, totalStartedThreadCountData);
				systemMonitorInfo.getData().put(CpuMeter.TOTAL_DEAMON_THREAD_COUNT, daemonThreadCountData);
				systemMonitorInfo.getData().put(CpuMeter.NUMBER_OF_THREADS, numberOfThreadsData);
				systemMonitorInfo.getData().put(CpuMeter.CPU_LOAD, totalCpuLoadData);
				systemMonitorInfo.getData().put(CpuMeter.APPLICATION_CPU_LOAD, applicationCpuLoadData);
				systemMonitorInfo.getData().put(CpuMeter.SYSTEM_CPU_LOAD, systemCpuLoadData);
			}
			else {
				systemMonitorInfo.setData(null);
			}

		}
		else {

			try {

				AbstractList<MonitorSample> systemCPUActivity = systemService.getSystemCpuActivity(systemMonitorInfo.getOffset(), systemMonitorInfo.getTimeRange(), systemMonitorInfo.getPeriod());

				if (systemCPUActivity != null) {

					Object[][] peakThreadCountData = new Object[systemCPUActivity.size()][2];
					Object[][] totalStartedThreadCountData = new Object[systemCPUActivity.size()][2];
					Object[][] daemonThreadCountData = new Object[systemCPUActivity.size()][2];
					Object[][] numberOfThreadsData = new Object[systemCPUActivity.size()][2];
					Object[][] totalCpuLoadData = new Object[systemCPUActivity.size()][2];
					Object[][] applicationCpuLoadData = new Object[systemCPUActivity.size()][2];
					Object[][] systemCpuLoadData = new Object[systemCPUActivity.size()][2];

					int i = 0;
					for (MonitorSample monitorSample : systemCPUActivity) {

						peakThreadCountData[i][0] = monitorSample.getTime();
						peakThreadCountData[i][1] = monitorSample.getData(CpuMeter.PEAK_THREAD_COUNT);
						totalStartedThreadCountData[i][0] = monitorSample.getTime();
						totalStartedThreadCountData[i][1] = monitorSample.getData(CpuMeter.TOTAL_STARTED_THREAD_COUNT);
						daemonThreadCountData[i][0] = monitorSample.getTime();
						daemonThreadCountData[i][1] = monitorSample.getData(CpuMeter.TOTAL_DEAMON_THREAD_COUNT);
						numberOfThreadsData[i][0] = monitorSample.getTime();
						numberOfThreadsData[i][1] = monitorSample.getData(CpuMeter.NUMBER_OF_THREADS);
						totalCpuLoadData[i][0] = monitorSample.getTime();
						totalCpuLoadData[i][1] = monitorSample.getData(CpuMeter.CPU_LOAD);
						applicationCpuLoadData[i][0] = monitorSample.getTime();
						applicationCpuLoadData[i][1] = monitorSample.getData(CpuMeter.APPLICATION_CPU_LOAD);
						systemCpuLoadData[i][0] = monitorSample.getTime();
						systemCpuLoadData[i][1] = monitorSample.getData(CpuMeter.SYSTEM_CPU_LOAD);

						i++;
					}

					systemMonitorInfo.getData().put(CpuMeter.PEAK_THREAD_COUNT, peakThreadCountData);
					systemMonitorInfo.getData().put(CpuMeter.TOTAL_STARTED_THREAD_COUNT, totalStartedThreadCountData);
					systemMonitorInfo.getData().put(CpuMeter.TOTAL_DEAMON_THREAD_COUNT, daemonThreadCountData);
					systemMonitorInfo.getData().put(CpuMeter.NUMBER_OF_THREADS, numberOfThreadsData);
					systemMonitorInfo.getData().put(CpuMeter.CPU_LOAD, totalCpuLoadData);
					systemMonitorInfo.getData().put(CpuMeter.APPLICATION_CPU_LOAD, applicationCpuLoadData);
					systemMonitorInfo.getData().put(CpuMeter.SYSTEM_CPU_LOAD, systemCpuLoadData);

				}
				else {
					systemMonitorInfo.setData(null);
				}

			} catch (ServerException e) {

				e.log(logger, false);
			}
		}

		cpuMeter.includeThread(threadId);

		return systemMonitorInfo;
	}

	/**
	 * Procesa una petición de información de la actividad de la CPU del sistema.
	 * 
	 * @param parameters Mapa de parámetros del que se obtienen el nombre, periodo y el corte temporal.
	 * 
	 * Nota: en caso de no indicar rango temporal o periodo, se informa únicamente con la última muestra recogida por el monitor.
	 *  
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/workload", method = { RequestMethod.POST })
	public @ResponseBody SystemMonitorInfoDto getWorkload(@RequestBody final Map<String, Object> parameters) {

		long threadId = Thread.currentThread().getId();

		cpuMeter.excludeThread(threadId);

		Number offset = (Number) parameters.get("offset");
		Number period = (Number) parameters.get("period");
		Number timeRange = (Number) parameters.get("timeRange");

		SystemMonitorInfoDto systemMonitorInfo = new SystemMonitorInfoDto();

		systemMonitorInfo.setName(WORKLOAD_MONITOR);
		if (offset != null) {
			systemMonitorInfo.setOffset(offset.longValue());
		}
		if (period != null) {
			systemMonitorInfo.setPeriod(period.longValue());
		}
		if (timeRange != null) {
			systemMonitorInfo.setTimeRange(timeRange.longValue());
		}

		if (systemMonitorInfo.getPeriod() == null || systemMonitorInfo.getTimeRange() == null) {

			MonitorSample workloadMonitorSample = systemService.getSystemWorkload();

			if (workloadMonitorSample != null) {

				Object[][] startTimeData = {{workloadMonitorSample.getData(WorkloadMeter.MEASURE_START_TIME)}};
				Object[][] requestsRateData = {{workloadMonitorSample.getData(WorkloadMeter.INCOMING_REQUESTS_RATE)}};
				Object[][] requestsRatePeakData = {{workloadMonitorSample.getData(WorkloadMeter.INCOMING_REQUESTS_RATE_PEAK)}};
				Object[][] totalRequestsData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_INCOMING_REQUESTS)}};
				Object[][] totalRequestsKoData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_INCOMING_REQUESTS_KO)}};
				Object[][] operationRateData = {{workloadMonitorSample.getData(WorkloadMeter.OPERATION_RATE)}};
				Object[][] operationRatePeakData = {{workloadMonitorSample.getData(WorkloadMeter.OPERATION_RATE_PEAK)}};
				Object[][] totalOperationsData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_OPERATIONS)}};
				Object[][] totalOperationsKoData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_OPERATIONS_KO)}};
				Object[][] subsystemOperationRateData = {{workloadMonitorSample.getData(WorkloadMeter.SUBSYSTEM_OPERATION_RATE)}};
				Object[][] subsystemOperationRatePeakData = {{workloadMonitorSample.getData(WorkloadMeter.SUBSYSTEM_OPERATION_RATE_PEAK)}};
				Object[][] totalSubsystemOperationsData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS)}};
				Object[][] totalSubsystemOpertionsKoData = {{workloadMonitorSample.getData(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS_KO)}};

				systemMonitorInfo.getData().put(WorkloadMeter.MEASURE_START_TIME, startTimeData);
				systemMonitorInfo.getData().put(WorkloadMeter.INCOMING_REQUESTS_RATE, requestsRateData);
				systemMonitorInfo.getData().put(WorkloadMeter.INCOMING_REQUESTS_RATE_PEAK, requestsRatePeakData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_INCOMING_REQUESTS, totalRequestsData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_INCOMING_REQUESTS_KO, totalRequestsKoData);
				systemMonitorInfo.getData().put(WorkloadMeter.OPERATION_RATE, operationRateData);
				systemMonitorInfo.getData().put(WorkloadMeter.OPERATION_RATE_PEAK, operationRatePeakData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_OPERATIONS, totalOperationsData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_OPERATIONS_KO, totalOperationsKoData);
				systemMonitorInfo.getData().put(WorkloadMeter.SUBSYSTEM_OPERATION_RATE, subsystemOperationRateData);
				systemMonitorInfo.getData().put(WorkloadMeter.SUBSYSTEM_OPERATION_RATE_PEAK, subsystemOperationRatePeakData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS, totalSubsystemOperationsData);
				systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS_KO, totalSubsystemOpertionsKoData);
			}
			else {
				systemMonitorInfo.setData(null);
			}

		}
		else {

			try {

				AbstractList<MonitorSample> systemWorkload = systemService.getSystemWorkload(systemMonitorInfo.getOffset(), systemMonitorInfo.getTimeRange(), systemMonitorInfo.getPeriod());

				if (systemWorkload != null) {

					Object[][] startTimeData = new Object[systemWorkload.size()][2];
					Object[][] requestsRateData = new Object[systemWorkload.size()][2];
					Object[][] requestsRatePeakData = new Object[systemWorkload.size()][2];
					Object[][] totalRequestsData = new Object[systemWorkload.size()][2];
					Object[][] totalRequestsKoData = new Object[systemWorkload.size()][2];
					Object[][] operationRateData = new Object[systemWorkload.size()][2];
					Object[][] operationRatePeakData = new Object[systemWorkload.size()][2];
					Object[][] totalOperationsData = new Object[systemWorkload.size()][2];
					Object[][] totalOperationsKoData = new Object[systemWorkload.size()][2];
					Object[][] subsystemOperationRateData = new Object[systemWorkload.size()][2];
					Object[][] subsystemOperationRatePeakData = new Object[systemWorkload.size()][2];
					Object[][] totalSubsystemOperationsData = new Object[systemWorkload.size()][2];
					Object[][] totalSubsystemOpertionsKoData = new Object[systemWorkload.size()][2];

					int i = 0;
					for (MonitorSample monitorSample : systemWorkload) {

						startTimeData[i][0] = monitorSample.getTime();
						startTimeData[i][1] = monitorSample.getData(WorkloadMeter.MEASURE_START_TIME);

						requestsRateData[i][0] = monitorSample.getTime();
						requestsRateData[i][1] = monitorSample.getData(WorkloadMeter.INCOMING_REQUESTS_RATE);

						requestsRatePeakData[i][0] = monitorSample.getTime();
						requestsRatePeakData[i][1] = monitorSample.getData(WorkloadMeter.INCOMING_REQUESTS_RATE_PEAK);

						totalRequestsData[i][0] = monitorSample.getTime();
						totalRequestsData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_INCOMING_REQUESTS);

						totalRequestsKoData[i][0] = monitorSample.getTime();
						totalRequestsKoData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_INCOMING_REQUESTS_KO);

						operationRateData[i][0] = monitorSample.getTime();
						operationRateData[i][1] = monitorSample.getData(WorkloadMeter.OPERATION_RATE);

						operationRatePeakData[i][0] = monitorSample.getTime();
						operationRatePeakData[i][1] = monitorSample.getData(WorkloadMeter.OPERATION_RATE_PEAK);

						totalOperationsData[i][0] = monitorSample.getTime();
						totalOperationsData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_OPERATIONS);

						totalOperationsKoData[i][0] = monitorSample.getTime();
						totalOperationsKoData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_OPERATIONS_KO);

						subsystemOperationRateData[i][0] = monitorSample.getTime();
						subsystemOperationRateData[i][1] = monitorSample.getData(WorkloadMeter.SUBSYSTEM_OPERATION_RATE);

						subsystemOperationRatePeakData[i][0] = monitorSample.getTime();
						subsystemOperationRatePeakData[i][1] = monitorSample.getData(WorkloadMeter.SUBSYSTEM_OPERATION_RATE_PEAK);

						totalSubsystemOperationsData[i][0] = monitorSample.getTime();
						totalSubsystemOperationsData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS);

						totalSubsystemOpertionsKoData[i][0] = monitorSample.getTime();
						totalSubsystemOpertionsKoData[i][1] = monitorSample.getData(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS_KO);

						i++;
					}

					systemMonitorInfo.getData().put(WorkloadMeter.MEASURE_START_TIME, startTimeData);
					systemMonitorInfo.getData().put(WorkloadMeter.INCOMING_REQUESTS_RATE, requestsRateData);
					systemMonitorInfo.getData().put(WorkloadMeter.INCOMING_REQUESTS_RATE_PEAK, requestsRatePeakData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_INCOMING_REQUESTS, totalRequestsData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_INCOMING_REQUESTS_KO, totalRequestsKoData);
					systemMonitorInfo.getData().put(WorkloadMeter.OPERATION_RATE, operationRateData);
					systemMonitorInfo.getData().put(WorkloadMeter.OPERATION_RATE_PEAK, operationRatePeakData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_OPERATIONS, totalOperationsData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_OPERATIONS_KO, totalOperationsKoData);
					systemMonitorInfo.getData().put(WorkloadMeter.SUBSYSTEM_OPERATION_RATE, subsystemOperationRateData);
					systemMonitorInfo.getData().put(WorkloadMeter.SUBSYSTEM_OPERATION_RATE_PEAK, subsystemOperationRatePeakData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS, totalSubsystemOperationsData);
					systemMonitorInfo.getData().put(WorkloadMeter.TOTAL_SUBSYSTEM_OPERATIONS_KO, totalSubsystemOpertionsKoData);
				}
				else {
					systemMonitorInfo.setData(null);
				}

			} catch (ServerException e) {

				e.log(logger, false);
			}
		}

		cpuMeter.includeThread(threadId);

		return systemMonitorInfo;
	}

	/**
	 * Procesa una petición de creción de un nuevo periodo de medidas para el medidor de trabajo realizado.
	 * 
	 * @return el momento de inicio del nuevo periodo en milisegundos desde epoch.
	 */
	@RequestMapping(value = "/workload/newMeasurePeriod", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Long newWorkloadMeasurePeriod() {

		return systemService.getWorkloadMeter().newMeasurePeriod();
	}

	/**
	 * Procesa una petición de información del estado de una caché.
	 * 
	 * @param parameters Mapa de parámetros del que se obtienen el nombre, periodo y el corte temporal.
	 * 
	 * Nota: en caso de no indicar rango temporal o periodo, se informa únicamente con la última muestra recogida por el monitor.
	 *  
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/cache", method = { RequestMethod.POST })
	public @ResponseBody SystemMonitorInfoDto getCache(@RequestBody final Map<String, Object> parameters) {

		long threadId = Thread.currentThread().getId();

		cpuMeter.excludeThread(threadId);

		Number offset = (Number) parameters.get("offset");
		Number period = (Number) parameters.get("period");
		Number timeRange = (Number) parameters.get("timeRange");
		String cacheManager = (String) parameters.get("cacheManager");
		String cache = (String) parameters.get("cache");

		SystemMonitorInfoDto systemMonitorInfo = new SystemMonitorInfoDto();

		systemMonitorInfo.setName(CACHE_MONITOR);
		if (offset != null) {
			systemMonitorInfo.setOffset(offset.longValue());
		}
		if (period != null) {
			systemMonitorInfo.setPeriod(period.longValue());
		}
		if (timeRange != null) {
			systemMonitorInfo.setTimeRange(timeRange.longValue());
		}

		if (systemMonitorInfo.getPeriod() == null || systemMonitorInfo.getTimeRange() == null) {

			MonitorSample cacheMonitorSample = systemService.getSystemCacheStatus(cacheManager, cache);

			if (cacheMonitorSample != null) {

				Object[] timestamp = {cacheMonitorSample.getTime()};
				Object[] totalHeapHitData = {cacheMonitorSample.getData(CacheMeter.TOTAL_HEAP_HIT)};
				Object[] totalHeapMissData = {cacheMonitorSample.getData(CacheMeter.TOTAL_HEAP_MISS)};
				Object[] heapHitRatioData = {cacheMonitorSample.getData(CacheMeter.TOTAL_HEAP_HIT_RATIO)};
				Object[] heapUsedSpaceData = {cacheMonitorSample.getData(CacheMeter.HEAP_USED_SPACE)};

				Object[] totalDiskHitData = {cacheMonitorSample.getData(CacheMeter.TOTAL_DISK_HIT)};
				Object[] totalDiskMissData = {cacheMonitorSample.getData(CacheMeter.TOTAL_DISK_MISS)};
				Object[] diskHitRatioData = {cacheMonitorSample.getData(CacheMeter.TOTAL_DISK_HIT_RATIO)};
				Object[] diskUsedSpaceData = {cacheMonitorSample.getData(CacheMeter.DISK_USED_SPACE)};

				systemMonitorInfo.getData().put(Meter.TIMESTAMP, timestamp);
				systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_HIT, totalHeapHitData);
				systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_MISS, totalHeapMissData);
				systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_HIT_RATIO, heapHitRatioData);
				systemMonitorInfo.getData().put(CacheMeter.HEAP_USED_SPACE, heapUsedSpaceData);

				systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_HIT, totalDiskHitData);
				systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_MISS, totalDiskMissData);
				systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_HIT_RATIO, diskHitRatioData);
				systemMonitorInfo.getData().put(CacheMeter.DISK_USED_SPACE, diskUsedSpaceData);
			}
			else {
				systemMonitorInfo.setData(null);
			}

		}
		else {

			try {

				AbstractList<MonitorSample> systemCacheStatus = systemService.getSystemCacheStatus(cacheManager, cache, 
						systemMonitorInfo.getOffset(), systemMonitorInfo.getTimeRange(), systemMonitorInfo.getPeriod());

				if (systemCacheStatus != null) {

					Object[] timestamp = new Object[systemCacheStatus.size()];
					Object[] totalHeapHitData = new Object[systemCacheStatus.size()];
					Object[] totalHeapMissData = new Object[systemCacheStatus.size()];
					Object[] heapHitRatioData = new Object[systemCacheStatus.size()];
					Object[] heapUsedSpaceData = new Object[systemCacheStatus.size()];

					Object[] totalDiskHitData = new Object[systemCacheStatus.size()];
					Object[] totalDiskMissData = new Object[systemCacheStatus.size()];
					Object[] diskHitRatioData = new Object[systemCacheStatus.size()];
					Object[] diskUsedSpaceData = new Object[systemCacheStatus.size()];

					int i = 0;
					for (MonitorSample monitorSample : systemCacheStatus) {

						timestamp[i] = monitorSample.getTime();
						totalHeapHitData[i] = monitorSample.getData(CacheMeter.TOTAL_HEAP_HIT);
						totalHeapMissData[i] = monitorSample.getData(CacheMeter.TOTAL_HEAP_MISS);
						heapHitRatioData[i] = monitorSample.getData(CacheMeter.TOTAL_HEAP_HIT_RATIO);
						heapUsedSpaceData[i] = monitorSample.getData(CacheMeter.HEAP_USED_SPACE);

						totalDiskHitData[i] = monitorSample.getData(CacheMeter.TOTAL_DISK_HIT);
						totalDiskMissData[i] = monitorSample.getData(CacheMeter.TOTAL_DISK_MISS);
						diskHitRatioData[i] = monitorSample.getData(CacheMeter.TOTAL_DISK_HIT_RATIO);
						diskUsedSpaceData[i] = monitorSample.getData(CacheMeter.DISK_USED_SPACE);

						i++;
					}

					systemMonitorInfo.getData().put(Meter.TIMESTAMP, timestamp);
					systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_HIT, totalHeapHitData);
					systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_MISS, totalHeapMissData);
					systemMonitorInfo.getData().put(CacheMeter.TOTAL_HEAP_HIT_RATIO, heapHitRatioData);
					systemMonitorInfo.getData().put(CacheMeter.HEAP_USED_SPACE, heapUsedSpaceData);

					systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_HIT, totalDiskHitData);
					systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_MISS, totalDiskMissData);
					systemMonitorInfo.getData().put(CacheMeter.TOTAL_DISK_HIT_RATIO, diskHitRatioData);
					systemMonitorInfo.getData().put(CacheMeter.DISK_USED_SPACE, diskUsedSpaceData);
				}
				else {
					systemMonitorInfo.setData(null);
				}

			} catch (ServerException e) {

				e.log(logger, false);
			}
		}

		cpuMeter.includeThread(threadId);

		return systemMonitorInfo;
	}
}
