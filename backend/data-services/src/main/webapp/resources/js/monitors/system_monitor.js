/**
 * Estructura global del monitor.
 */
var cpuHistoryChart;
var cpuInstantChart;
var threadsHistoryChart;
var threadsInstantChart;
var memoryHistoryChart;
var memoryInstantCommitedChart;
var memoryInstantUsedChart;


/**
 * Inicializa el monitor de CPU.
 * 
 * @param cpuHistory Nombre del gráfico histórico de CPU.
 * @param cpuInstant Nombre del gráfico de CPU del instante más reciente.
 * @param threadsHistory Nombre del gráfico histórico de hilos de ejecución.
 * @param threadsInstant Nombre del gráfico de hilos de ejecución en el instante más reciente.
 * @param memoryHistory Nombre del elemento en el que se renderiza el histórico de memoria.
 * @param memoryInstantCommited Nombre del elemento en el que se renderiza el instante más reciente de memoria comprometida.
 * @param memoryInstantUsed Nombre del elemento en el que se renderiza el instante más reciente de memoria usada.
 */
function initSystemMonitorMonitor(cpuHistory, cpuInstant, threadsHistory, threadsInstant, memoryHistory, memoryInstantCommited, memoryInstantUsed) {

	cpuHistoryChart = ChartHelper(cpuHistory);
	cpuInstantChart = ChartHelper(cpuInstant);
	threadsHistoryChart = ChartHelper(threadsHistory);
	threadsInstantChart = ChartHelper(threadsInstant);
	memoryHistoryChart = ChartHelper(memoryHistory);
	memoryInstantCommitedChart = ChartHelper(memoryInstantCommited);
	memoryInstantUsedChart = ChartHelper(memoryInstantUsed);
}

/**
 * Alimentador de información de las gráficas de actividad de la CPU.
 * Petición completa de las gráficas según la ventana de tiempo definida.
 * 
 * @param systemMonitorWindow Objeto que contiene la ventana de tiempo a monitorizar.
 * @param isRefresh Null o false en caso de que se trate de una petición de creción de la gráfica incial. 
 * 					True en caso de que se trate de un refresco.
 */
function systemCpuMonitorRequestWindow(systemMonitorWindow, isRefresh) {

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + "/system/cpu", false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	// Envío.
	xhr.send(systemMonitorWindow.getWindowRequestJSON());

	// Lectura de la respuesta.
	var jsonResponse = JSON.parse(xhr.responseText);

	if (jsonResponse["data"] != null && jsonResponse["data"]["Timestamp"]) {

		if (isRefresh == null || !isRefresh) {

			var responseTimestamps = jsonResponse["data"]["Timestamp"];
			var labels = new Array(responseTimestamps.length);

			var responsePeakThreadCount = jsonResponse["data"]["PeakThreadCount"]; //Pico máximo de threads activos.
			var responseTotalStartedThreadCount = jsonResponse["data"]["TotalStartedThreadCount"]; //Número de threads inciados desde el arranque de la aplicación.
			var responseDaemonThreadCount = jsonResponse["data"]["DaemonThreadCount"]; //Número de threads de tipo demonio activos.
			var responseNumberOfThreads = jsonResponse["data"]["NumberOfThreads"]; //Número de threads activos.

			var responseSystemCPULoad = jsonResponse["data"]["SystemCPULoad"]; //Carga de la CPU por procesos del sistema.
			var responseApplicationCPULoad = jsonResponse["data"]["ApplicationCPULoad"]; //Carga de la CPU por procesos de la aplicación.
			var responseCPULoad = jsonResponse["data"]["CPULoad"]; //Carga general de la CPU.


			for (var i=0; i<responseTimestamps.length; i++) {

				var date = new Date(responseTimestamps[i]);
				labels[i] = date.toLocaleString();
			}

			// Histórico de uso de CPU.
			if (cpuHistoryChart != null) {

				cpuHistoryChartData.labels = labels;
				cpuHistoryChartData.datasets[0].data = responseCPULoad;
				cpuHistoryChartData.datasets[1].data = responseSystemCPULoad;
				cpuHistoryChartData.datasets[2].data = responseApplicationCPULoad;

				cpuHistoryChart.createChart("line", cpuHistoryChartOptions, cpuHistoryChartData);
			}

			// Uso de CPU en el instante más reciente.
			if (cpuInstantChart != null) {

				cpuInstantChartData[0].value = 100 - responseCPULoad[responseCPULoad.length-1];
				cpuInstantChartData[1].value = responseSystemCPULoad[responseSystemCPULoad.length-1];
				cpuInstantChartData[2].value = responseApplicationCPULoad[responseApplicationCPULoad.length-1];

				cpuInstantChart.createChart("pie", cpuInstantChartOptions, cpuInstantChartData);
			}

			// Histórico de hilos de ejecución.
			if (threadsHistoryChart != null) {

				threadsHistoryChartData.labels = labels;
				threadsHistoryChartData.datasets[0].data = responsePeakThreadCount;
				threadsHistoryChartData.datasets[1].data = responseNumberOfThreads;
				threadsHistoryChartData.datasets[2].data = responseDaemonThreadCount;

				threadsHistoryChart.createChart("line", threadsHistoryChartOptions, threadsHistoryChartData);
			}

			// Hilos de ejecución en el instante más reciente.
			if (threadsInstantChart != null) {
				var daemonThreads = responseDaemonThreadCount[responseDaemonThreadCount.length-1];

				threadsInstantChartData[0].value = responseNumberOfThreads[responseNumberOfThreads.length-1] - daemonThreads;
				threadsInstantChartData[1].value = daemonThreads;

				threadsInstantChart.createChart("pie", threadsInstantChartOptions, threadsInstantChartData);
			}
		}
		else if (jsonResponse["data"]["Timestamp"].length > 0) {

			var date = new Date(jsonResponse["data"]["Timestamp"][0]);
			var label = date.toLocaleString();

			// Histórico de uso de CPU.
			if (cpuHistoryChart != null) {

				var data = [jsonResponse["data"]["CPULoad"][0], jsonResponse["data"]["SystemCPULoad"][0], jsonResponse["data"]["ApplicationCPULoad"][0]];

				cpuHistoryChart.chart.options.animation = false;
				cpuHistoryChart.setData(label, data, true);
			}

			// Uso de CPU en el instante más reciente.
			if (cpuInstantChart != null) {

				var cpuLoad = 100 - jsonResponse["data"]["CPULoad"][0];
				var systemCPULoad = jsonResponse["data"]["SystemCPULoad"][0];
				var applicationCPULoad = jsonResponse["data"]["ApplicationCPULoad"][0];

				cpuInstantChart.chart.options.animation = false;
				cpuInstantChart.setData(null, [cpuLoad, systemCPULoad, applicationCPULoad], true);
			}

			// Histórico de hilos de ejecución.
			if (threadsHistoryChart != null) {

				var data = [jsonResponse["data"]["PeakThreadCount"][0], jsonResponse["data"]["NumberOfThreads"][0], jsonResponse["data"]["DaemonThreadCount"][0]];

				threadsHistoryChart.chart.options.animation = false;
				threadsHistoryChart.setData(label, data, true);
			}

			// Hilos de ejecución en el instante más reciente.
			if (threadsInstantChart != null) {

				var daemonThreads = jsonResponse["data"]["DaemonThreadCount"][0];
				var nonDaemonThreads = jsonResponse["data"]["NumberOfThreads"][0] - daemonThreads;

				threadsInstantChart.chart.options.animation = false;
				threadsInstantChart.setData(null, [nonDaemonThreads, daemonThreads], true);
			}
		}
	}
}

/**
 * Alimentador de información de las gráficas de actividad de la memoria.
 * Petición completa de las gráficas según la ventana de tiempo definida.
 * 
 * @param systemMonitorWindow Objeto que contiene la ventana de tiempo a monitorizar.
 * @param isRefresh Null o false en caso de que se trate de una petición de creción de la gráfica incial. 
 * 					True en caso de que se trate de un refresco.
 */
function systemMemoryMonitorRequestWindow(systemMonitorWindow, isRefresh) {

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + "/system/memory", false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	// Envío.
	xhr.send(systemMonitorWindow.getWindowRequestJSON());

	// Lectura de la respuesta.
	var jsonResponse = JSON.parse(xhr.responseText);

	if (jsonResponse["data"] != null && jsonResponse["data"]["Timestamp"]) {

		if (isRefresh == null || !isRefresh) {

			var responseTimestamps = jsonResponse["data"]["Timestamp"];
			var labels = new Array(responseTimestamps.length);

			var responseTotalMaxMemory = jsonResponse["data"]["TotalMaxMemory"]; //Memoria máxima de la que dispone la instancia.
			var responseTotalInitMemory = jsonResponse["data"]["TotalInitMemory"]; //Memoria inicial con la que se arranca la instancia.
			var responseTotalCommitted = jsonResponse["data"]["TotalCommitted"]; //Memoria reservada y disponible para ser usada.

			var responseTotalUsedMemory = jsonResponse["data"]["TotalUsedMemory"]; //Memoria total usada.
			var responseUsedHeapMemory = jsonResponse["data"]["UsedHeapMemory"]; //Tamaño dedicado al Heap (Datos dinámicos).
			var responseUsedNonHeapMemory = jsonResponse["data"]["UsedNonHeapMemory"]; //Tamaño no dedicado al Heap (Datos estáticos, código, ...etc.).

			for (var i=0; i<responseTimestamps.length; i++) {

				var date = new Date(responseTimestamps[i]);
				labels[i] = date.toLocaleString();
			}

			// Histórico de uso de memoria.
			if (memoryHistoryChart != null) {

				memoryHistoryChartData.labels = labels;
				memoryHistoryChartData.datasets[0].data = responseTotalMaxMemory;
				memoryHistoryChartData.datasets[1].data = responseTotalInitMemory;
				memoryHistoryChartData.datasets[2].data = responseTotalCommitted;
				memoryHistoryChartData.datasets[3].data = responseTotalUsedMemory;
				memoryHistoryChartData.datasets[4].data = responseUsedHeapMemory;
				memoryHistoryChartData.datasets[5].data = responseUsedNonHeapMemory;

				memoryHistoryChart.createChart("line", memoryHistoryChartOptions, memoryHistoryChartData);
			}

			// Memoria comprometida en el instante más reciente.
			if (memoryInstantCommitedChart != null) {

				memoryInstantCommitedChartData.datasets[0].data[0] = responseTotalMaxMemory[responseTotalMaxMemory.length-1] - responseTotalCommitted[responseTotalCommitted.length-1];
				memoryInstantCommitedChartData.datasets[1].data[0] = responseTotalCommitted[responseTotalCommitted.length-1];
				
				memoryInstantCommitedChart.createChart("stackedBar", memoryInstantCommitedChartOptions, memoryInstantCommitedChartData);
			}
			
			// Uso de memoria en el instante más reciente.
			if (memoryInstantUsedChart != null) {
				
				memoryInstantUsedChartData.datasets[0].data[0] = responseTotalCommitted[responseTotalCommitted.length-1] - responseUsedHeapMemory[responseUsedHeapMemory.length-1] - responseUsedNonHeapMemory[responseUsedNonHeapMemory.length-1];
				memoryInstantUsedChartData.datasets[1].data[0] = responseUsedHeapMemory[responseUsedHeapMemory.length-1];
				memoryInstantUsedChartData.datasets[2].data[0] = responseUsedNonHeapMemory[responseUsedNonHeapMemory.length-1];
				
				memoryInstantUsedChart.createChart("stackedBar", memoryInstantUsedChartOptions, memoryInstantUsedChartData);
			}
		}
		else if (jsonResponse["data"]["Timestamp"].length > 0) {

			var date = new Date(jsonResponse["data"]["Timestamp"][0]);
			var label = date.toLocaleString();

			// Histórico de uso de memoria.
			if (memoryHistoryChart != null) {

				var responseTotalMaxMemory = jsonResponse["data"]["TotalMaxMemory"]; //Memoria máxima de la que dispone la instancia.
				var responseTotalInitMemory = jsonResponse["data"]["TotalInitMemory"]; //Memoria inicial con la que se arranca la instancia.
				var responseTotalCommitted = jsonResponse["data"]["TotalCommitted"]; //Memoria reservada y disponible para ser usada.

				var responseTotalUsedMemory = jsonResponse["data"]["TotalUsedMemory"]; //Memoria total usada.
				var responseUsedHeapMemory = jsonResponse["data"]["UsedHeapMemory"]; //Tamaño dedicado al Heap (Datos dinámicos).
				var responseUsedNonHeapMemory = jsonResponse["data"]["UsedNonHeapMemory"]; //Tamaño no dedicado al Heap (Datos estáticos, código, ...etc.).


				var data = [jsonResponse["data"]["TotalMaxMemory"][0], 
				            jsonResponse["data"]["TotalInitMemory"][0], 
				            jsonResponse["data"]["TotalCommitted"][0],
				            jsonResponse["data"]["TotalUsedMemory"][0],
				            jsonResponse["data"]["UsedHeapMemory"][0],
				            jsonResponse["data"]["UsedNonHeapMemory"][0]
				];

				memoryHistoryChart.chart.options.animation = false;
				memoryHistoryChart.setData(label, data, true);
			}

			// Memoria comprometida en el instante más reciente.
			if (memoryInstantCommitedChart != null) {

				var totalMaxMemory = jsonResponse["data"]["TotalMaxMemory"][0];
				var totalCommitted = jsonResponse["data"]["TotalCommitted"][0];
				
				memoryInstantCommitedChart.setData(null, [[totalMaxMemory - totalCommitted], [totalCommitted]], true);
			}
			
			// Uso de memoria en el instante más reciente.
			if (memoryInstantUsedChart != null) {
				
				var totalCommitted = jsonResponse["data"]["TotalCommitted"][0];
				var usedHeapMemory = jsonResponse["data"]["UsedHeapMemory"][0];
				var usedNonHeapMemory = jsonResponse["data"]["UsedNonHeapMemory"][0];
				
				memoryInstantUsedChart.setData(null, [[totalCommitted - usedHeapMemory - usedNonHeapMemory], [usedHeapMemory], [usedNonHeapMemory]], true);
			}
		}
	}
}

/**
 * Detiene el monitor de systema.
 */
function stopSystemMonitor() {

	if (App.systemMonitorInterval) {

		clearInterval(App.systemMonitorInterval);
		App.systemMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de systema.
 */
function startSystemMonitor(systemMonitorWindow) {

	stopSystemMonitor();

	App.systemMonitorInterval = setInterval(function() {

		// Avance para únicamente obtener el siguiente periodo.
		systemMonitorWindow.moveToNextPeriod();
		systemCpuMonitorRequestWindow(systemMonitorWindow, true);
		systemMemoryMonitorRequestWindow(systemMonitorWindow, true);

	}, systemMonitorWindow.period * 1000);
}
