/**
 * Estructura global del monitor.
 */
var cacheHAHeapHistoryChart;
var cacheHAHeapChart;

var cacheHADiskHistoryChart;
var cacheHADiskChart;

var cacheHeapHistoryChart;
var cacheHeapChart;

var cacheDiskHistoryChart;
var cacheDiskChart;

var maxLocalHeap;
var maxLocalDisk;


/**
 * Inicializa el monitor de Caché.
 * 
 * @param maxBytesLocalHeap Tamaño máximo de la caché en memoria, expresado en bytes.
 * @param maxBytesLocalDisk Tamaño máximo de la caché en disco, expresado en bytes.
 * 
 * Mota: Los siquientes parámetros contienen el nombre del contenedor que incluye el canvas y el div para la leyenda de cada gráfico.
 * 
 * @param cacheHAHeapHistory Nombre del gráfico histórico de Hits y Adds a nivel de memoria.
 * @param cacheHAHeap Nombre del gráfico de Hits y Adds a nivel de memoria.
 * @param cacheHADiskHistory Nombre del gráfico histórico de Hits y Adds a nivel de disco.
 * @param cacheHADisk Nombre del gráfico de Hits y Adds a nivel de disco.
 * @param cacheHeapHistory Nombre del gráfico histórico de consumo a nivel de memoria.
 * @param cacheHeap Nombre del gráfico histórico de consumo a nivel de memoria.
 * @param cacheDiskHistory Nombre del gráfico histórico de consumo a nivel de disco.
 * @param cacheDisk Nombre del gráfico histórico de consumo a nivel de disco.
 */
function initCacheMonitor(maxBytesLocalHeap, maxBytesLocalDisk,
		cacheHAHeapHistory, cacheHAHeap, 
		cacheHADiskHistory, cacheHADisk, 
		cacheHeapHistory, cacheHeap, 
		cacheDiskHistory, cacheDisk) {

	maxLocalHeap = maxBytesLocalHeap;
	maxLocalDisk = maxBytesLocalDisk;

	cacheHAHeapHistoryChart = ChartHelper(cacheHAHeapHistory);
	cacheHAHeapChart = ChartHelper(cacheHAHeap);

	cacheHADiskHistoryChart = ChartHelper(cacheHADiskHistory);
	cacheHADiskChart = ChartHelper(cacheHADisk);

	cacheHeapHistoryChart = ChartHelper(cacheHeapHistory);
	cacheHeapChart = ChartHelper(cacheHeap);

	cacheDiskHistoryChart = ChartHelper(cacheDiskHistory);
	cacheDiskChart = ChartHelper(cacheDisk);
}

/**
 * Alimentador de información de las gráficas de actividad de la caché.
 * Petición completa de las gráficas según la ventana de tiempo definida.
 * 
 * @param cacheMonitorWindow Objeto que contiene la ventana de tiempo a monitorizar.
 * @param isRefresh Null o false en caso de que se trate de una petición de creción de la gráfica incial. 
 * 					True en caso de que se trate de un refresco.
 */
function monitorRequestWindow(cacheMonitorWindow, isRefresh) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + "/system/cache", false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	// Envío.
	xhr.send(cacheMonitorWindow.getWindowRequestJSON());

	// Lectura de la respuesta.
	var jsonResponse = JSON.parse(xhr.responseText);

	if (jsonResponse["data"] != null) {

		if (isRefresh == null || !isRefresh) {

			var responseTimestamps = jsonResponse["data"]["Timestamp"];
			var labels = new Array(responseTimestamps.length);

			var totalHeapHits = 0;
			var totalHeapMisses = 0;
			var totalDiskHits = 0;
			var totalDiskMisses = 0;
			var totalUsedHeapSpace = 0;
			var totalUsedDiskSpace = 0;

			var responseTotalHeapHit = jsonResponse["data"]["TotalHeapHit"];
			var responseTotalHeapMiss = jsonResponse["data"]["TotalHeapMiss"];
			var responseTotalDiskHit = jsonResponse["data"]["TotalDiskHit"];
			var responseTotalDiskMiss = jsonResponse["data"]["TotalDiskMiss"];
			var responseUsedHeapSpace = jsonResponse["data"]["UsedHeapSpace"];
			var responseUsedDiskSpace = jsonResponse["data"]["UsedDiskSpace"];

			for (var i=0; i<responseTimestamps.length; i++) {

				var date = new Date(responseTimestamps[i]);
				labels[i] = date.toLocaleString();

				totalHeapHits += responseTotalHeapHit[i]; 
				totalHeapMisses += responseTotalHeapMiss[i]; 
				totalDiskHits += responseTotalDiskHit[i];
				totalDiskMisses += responseTotalDiskMiss[i];
				totalUsedHeapSpace += responseUsedHeapSpace[i];
				totalUsedDiskSpace += responseUsedDiskSpace[i];
			}

			// Accesos a memoria.
			if (cacheHAHeapHistoryChart != null) {

				cacheHAHeapHistoryChartData.labels = labels;
				cacheHAHeapHistoryChartData.datasets[0].data = responseTotalHeapHit;
				cacheHAHeapHistoryChartData.datasets[1].data = responseTotalHeapMiss;

				cacheHAHeapHistoryChart.createChart("line", cacheHAHeapHistoryChartOptions, cacheHAHeapHistoryChartData);

				if (cacheHAHeapChart != null) {

					cacheHAHeapChartData[0].value = totalHeapHits;
					cacheHAHeapChartData[1].value = totalHeapMisses;

					cacheHAHeapChart.createChart("pie", cacheHAHeapChartOptions, cacheHAHeapChartData);
				}
			}

			// Accesos a disco.
			if (cacheHADiskHistoryChart != null) {

				cacheHADiskHistoryChartData.labels = labels;
				cacheHADiskHistoryChartData.datasets[0].data = responseTotalDiskHit;
				cacheHADiskHistoryChartData.datasets[1].data = responseTotalDiskMiss;

				cacheHADiskHistoryChart.createChart("line", cacheHADiskHistoryChartOptions, cacheHADiskHistoryChartData);

				if (cacheHADiskChart != null) {

					cacheHADiskChartData[0].value = totalDiskHits;
					cacheHADiskChartData[1].value = totalDiskMisses;

					cacheHADiskChart.createChart("pie", cacheHADiskChartOptions, cacheHADiskChartData);
				}
			}

			// Uso de memoria.
			if (cacheHeapHistoryChart != null) {

				cacheHeapHistoryChartData.heapSpaceUseSummatory = totalUsedHeapSpace; 
				cacheHeapHistoryChartData.heapSpaceUseSamples = labels.length; 
				cacheHeapHistoryChartData.labels = labels;
				cacheHeapHistoryChartData.datasets[0].data = responseUsedHeapSpace;

				cacheHeapHistoryChart.createChart("line", cacheHeapHistoryChartOptions, cacheHeapHistoryChartData);

				if (cacheHeapChart != null) {

					var avgHeapSpaceUse = cacheHeapHistoryChartData.heapSpaceUseSummatory / cacheHeapHistoryChartData.heapSpaceUseSamples;
					var freeHeapSpace = maxLocalHeap - avgHeapSpaceUse

					if (freeHeapSpace < 0) {
						freeHeapSpace = 0;
					}

					cacheHeapChartData[0].value = avgHeapSpaceUse;
					cacheHeapChartData[1].value = freeHeapSpace;

					cacheHeapChart.createChart("pie", cacheHeapChartOptions, cacheHeapChartData);
				}
			}

			// Uso de disco.
			if (cacheDiskHistoryChart != null) {

				cacheDiskHistoryChartData.diskSpaceUseSummatory = totalUsedDiskSpace; 
				cacheDiskHistoryChartData.diskSpaceUseSamples = labels.length; 
				cacheDiskHistoryChartData.labels = labels;
				cacheDiskHistoryChartData.datasets[0].data = responseUsedDiskSpace;

				cacheDiskHistoryChart.createChart("line", cacheDiskHistoryChartOptions, cacheDiskHistoryChartData);

				if (cacheDiskChart != null) {

					var avgDiskSpaceUse = cacheDiskHistoryChartData.diskSpaceUseSummatory / cacheDiskHistoryChartData.diskSpaceUseSamples;
					var freeDiskSpace = maxLocalDisk - avgDiskSpaceUse

					if (freeDiskSpace < 0) {
						freeDiskSpace = 0;
					}

					cacheDiskChartData[0].value = avgDiskSpaceUse;
					cacheDiskChartData[1].value = freeDiskSpace;

					cacheDiskChart.createChart("pie", cacheDiskChartOptions, cacheDiskChartData);
				}
			}
		}
		else if (jsonResponse["data"]["Timestamp"].length > 0) {

			var date = new Date(jsonResponse["data"]["Timestamp"][0]);
			var label = date.toLocaleString();

			// Accesos a memoria.
			if (cacheHAHeapHistoryChart != null) {

				var data = [jsonResponse["data"]["TotalHeapHit"][0], jsonResponse["data"]["TotalHeapMiss"][0]];

				cacheHAHeapHistoryChart.chart.options.animation = false;
				var removedData = cacheHAHeapHistoryChart.setData(label, data, true);

				if (cacheHAHeapChart != null) {

					var totalHeapHits = cacheHAHeapChartData[0].value - removedData[0] + data[0];
					var totalHeapMisses = cacheHAHeapChartData[1].value - removedData[1] + data[1];

					cacheHAHeapChart.chart.options.animation = false;
					cacheHAHeapChart.setData(null, [totalHeapHits, totalHeapMisses], true);
				}
			}

			// Accesos a disco.
			if (cacheHADiskHistoryChart != null) {

				var data = [jsonResponse["data"]["TotalDiskHit"][0], jsonResponse["data"]["TotalDiskMiss"][0]];

				cacheHADiskHistoryChart.chart.options.animation = false;
				var removedData = cacheHADiskHistoryChart.setData(label, data, true);

				if (cacheHADiskChart != null) {

					var totalDiskHits = cacheHADiskChartData[0].value - removedData[0] + data[0];
					var totalDiskMisses = cacheHADiskChartData[1].value - removedData[1] + data[1];

					cacheHADiskChart.chart.options.animation = false;
					cacheHADiskChart.setData(null, [totalDiskHits, totalDiskMisses], true);
				}
			}

			// Uso de memoria.
			if (cacheHeapHistoryChart != null) {

				var data = [jsonResponse["data"]["UsedHeapSpace"][0]];

				cacheHeapHistoryChart.chart.options.animation = false;
				var removedData = cacheHeapHistoryChart.setData(label, data, true);

				cacheHeapHistoryChartData.heapSpaceUseSummatory += data - removedData[0];

				if (cacheHeapChart != null) {

					cacheHeapChart.chart.options.animation = false;
					var avgHeapSpaceUse = cacheHeapHistoryChartData.heapSpaceUseSummatory / cacheHeapHistoryChartData.heapSpaceUseSamples;
					var freeHeapSpace = maxLocalHeap - avgHeapSpaceUse

					if (freeHeapSpace < 0) {
						freeHeapSpace = 0;
					}

					cacheHeapChart.setData(null, [avgHeapSpaceUse, freeHeapSpace], true);
				}
			}

			// Uso de disco.
			if (cacheDiskHistoryChart != null) {

				var data = [jsonResponse["data"]["UsedDiskSpace"][0]];

				cacheDiskHistoryChart.chart.options.animation = false;
				var removedData = cacheDiskHistoryChart.setData(label, data, true);

				cacheDiskHistoryChartData.diskSpaceUseSummatory += data - removedData[0];

				if (cacheDiskChart != null) {

					cacheDiskChart.chart.options.animation = false;
					var avgDiskSpaceUse = cacheDiskHistoryChartData.diskSpaceUseSummatory / cacheDiskHistoryChartData.diskSpaceUseSamples;
					var freeDiskSpace = maxLocalDisk - avgDiskSpaceUse

					if (freeDiskSpace < 0) {
						freeDiskSpace = 0;
					}

					cacheDiskChart.setData(null, [avgDiskSpaceUse, freeDiskSpace], true);
				}
			}
		}
	}
}

/**
 * Detiene el monitor de caché.
 */
function stopCacheMonitor() {

	if (App.cacheMonitorInterval) {

		clearInterval(App.cacheMonitorInterval);
		App.cacheMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de caché.
 */
function startCacheMonitor(cacheMonitorWindow) {

	stopCacheMonitor();

	App.cacheMonitorInterval = setInterval(function() {

		// Avance para únicamente obtener el siguiente periodo.
		cacheMonitorWindow.moveToNextPeriod();
		monitorRequestWindow(cacheMonitorWindow, true);

	}, cacheMonitorWindow.period * 1000);
}
