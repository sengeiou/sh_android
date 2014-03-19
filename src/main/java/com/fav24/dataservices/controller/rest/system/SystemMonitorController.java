package com.fav24.dataservices.controller.rest.system;


import java.util.AbstractList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fav24.dataservices.controller.rest.BaseRestController;
import com.fav24.dataservices.dto.system.JqPlotDto;
import com.fav24.dataservices.monitoring.MemoryMeter;
import com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample;
import com.fav24.dataservices.service.system.SystemService;

/**
 * Controla las peticiones de entrada a los servicios de gestión de la seguridad.
 * 
 * @author Fav24
 */
@Controller
@RequestMapping("/system")
public class SystemMonitorController extends BaseRestController {

	final static Logger logger = LoggerFactory.getLogger(SystemMonitorController.class);

	public static final Long DEFAULT_PERIOD = 1L; // Resolución por defecto de la información en segundos.
	public static final Long DEFAULT_TIME_RANGE = 300L; // Rango de la información en segundos. ( los últimos 5 minutos.)

	public static final String MEMORY_MONITOR = "MemoryMonitor";
	public static final String CPU_MONITOR = "CPUMonitor";
	public static final String STORAGE_MONITOR = "StorageMonitor";

	@Autowired
	protected SystemService systemService;


	/**
	 * Procesa una petición de información del estado de la memoria del sistema.
	 * 
	 * @param jqPlot Elemento a poblar con la información pendiente. De él se obtienen el periodo y el corte temporal.
	 * 
	 * Nota: en caso de no indicar rango temporal o periodo, se informa únicamente con la última muestra recogida por el monitor.
	 *  
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/memory", method = { RequestMethod.POST })
	public @ResponseBody JqPlotDto getMemory(@RequestBody final JqPlotDto jqPlot) {

		jqPlot.setName(MEMORY_MONITOR);

		if (jqPlot.getPeriod() == null || jqPlot.getTimeRange() == null) {

			MonitorSample memoryMonitorSample = systemService.getSystemMemoryStatus();

			Object[][] totalInitMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_INIT_MEMORY)}};
			Object[][] totalMaxMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_MAX_MEMORY)}};
			Object[][] totalCommittedMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_COMMITTED_MEMORY)}};
			Object[][] totalUsedMemoryData = {{memoryMonitorSample.getData(MemoryMeter.TOTAL_USED_MEMORY)}};
			Object[][] UsedHeapMemoryData = {{memoryMonitorSample.getData(MemoryMeter.USED_HEAP_MEMORY)}};
			Object[][] UsedNonHeapMemoryData = {{memoryMonitorSample.getData(MemoryMeter.USED_NONHEAP_MEMORY)}};
			
			jqPlot.getData().put(MemoryMeter.TOTAL_INIT_MEMORY, totalInitMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_MAX_MEMORY, totalMaxMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_COMMITTED_MEMORY, totalCommittedMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_USED_MEMORY, totalUsedMemoryData);
			jqPlot.getData().put(MemoryMeter.USED_HEAP_MEMORY, UsedHeapMemoryData);
			jqPlot.getData().put(MemoryMeter.USED_NONHEAP_MEMORY, UsedNonHeapMemoryData);
		}
		else {

			AbstractList<MonitorSample> systemMemoryStatus = systemService.getSystemMemoryStatus(jqPlot.getPeriod(), jqPlot.getTimeRange());

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

			jqPlot.getData().put(MemoryMeter.TOTAL_INIT_MEMORY, totalInitMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_MAX_MEMORY, totalMaxMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_COMMITTED_MEMORY, totalCommittedMemoryData);
			jqPlot.getData().put(MemoryMeter.TOTAL_USED_MEMORY, totalUsedMemoryData);
			jqPlot.getData().put(MemoryMeter.USED_HEAP_MEMORY, UsedHeapMemoryData);
			jqPlot.getData().put(MemoryMeter.USED_NONHEAP_MEMORY, UsedNonHeapMemoryData);
		}

		return jqPlot;
	}
}
