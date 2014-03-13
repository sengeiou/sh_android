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
	 * @return el resultado del procesado de la petición.
	 */
	@RequestMapping(value = "/memory", method = { RequestMethod.POST })
	public @ResponseBody JqPlotDto getMemory(@RequestBody final JqPlotDto jqPlot) {

		Long period = jqPlot.getPeriod() == null ? DEFAULT_PERIOD : jqPlot.getPeriod();
		Long timeRange = jqPlot.getTimeRange() == null ? DEFAULT_TIME_RANGE : jqPlot.getTimeRange();

		AbstractList<MonitorSample> systemMemoryStatus = systemService.getSystemMemoryStatus(period, timeRange);

		JqPlotDto result = new JqPlotDto();

		result.setPeriod(period);
		result.setTimeRange(timeRange);
		result.setName(MEMORY_MONITOR);

		Object[][] totalInitMemorydata = new Object[systemMemoryStatus.size()][2];
		Object[][] totalMaxMemorydata = new Object[systemMemoryStatus.size()][2];
		Object[][] totalCommittedMemorydata = new Object[systemMemoryStatus.size()][2];
		Object[][] totalUsedMemorydata = new Object[systemMemoryStatus.size()][2];
		Object[][] UsedHeapMemorydata = new Object[systemMemoryStatus.size()][2];
		Object[][] UsedNonHeapMemorydata = new Object[systemMemoryStatus.size()][2];

		result.getData().put(MemoryMeter.TOTAL_INIT_MEMORY, totalInitMemorydata);
		result.getData().put(MemoryMeter.TOTAL_MAX_MEMORY, totalMaxMemorydata);
		result.getData().put(MemoryMeter.TOTAL_COMMITTED_MEMORY, totalCommittedMemorydata);
		result.getData().put(MemoryMeter.TOTAL_USED_MEMORY, totalUsedMemorydata);
		result.getData().put(MemoryMeter.USED_HEAP_MEMORY, UsedHeapMemorydata);
		result.getData().put(MemoryMeter.USED_NONHEAP_MEMORY, UsedNonHeapMemorydata);

		int i = 0;
		for (MonitorSample monitorSample : systemMemoryStatus) {

			totalInitMemorydata[i][0] = monitorSample.getTime();
			totalInitMemorydata[i][1] = monitorSample.getData(MemoryMeter.TOTAL_INIT_MEMORY);
			totalMaxMemorydata[i][0] = monitorSample.getTime();
			totalMaxMemorydata[i][1] = monitorSample.getData(MemoryMeter.TOTAL_MAX_MEMORY);
			totalCommittedMemorydata[i][0] = monitorSample.getTime();
			totalCommittedMemorydata[i][1] = monitorSample.getData(MemoryMeter.TOTAL_COMMITTED_MEMORY);
			totalUsedMemorydata[i][0] = monitorSample.getTime();
			totalUsedMemorydata[i][1] = monitorSample.getData(MemoryMeter.TOTAL_USED_MEMORY);
			UsedHeapMemorydata[i][0] = monitorSample.getTime();
			UsedHeapMemorydata[i][1] = monitorSample.getData(MemoryMeter.USED_HEAP_MEMORY);
			UsedNonHeapMemorydata[i][0] = monitorSample.getTime();
			UsedNonHeapMemorydata[i][1] = monitorSample.getData(MemoryMeter.USED_NONHEAP_MEMORY);

			i++;
		}

		return result;
	}
}
