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

	if (isRefresh == null || !isRefresh) {

		// Accesos a memoria.
		if (cacheHAHeapHistoryChart != null) {

			var totalHeapHits = 0;
			var totalHeapMisses = 0;

			var responseTotalHeapHit = jsonResponse["data"]["TotalHeapHit"];
			var responseTotalHeapMiss = jsonResponse["data"]["TotalHeapMiss"];

			var length = responseTotalHeapHit.length < responseTotalHeapMiss.length ? responseTotalHeapHit.length : responseTotalHeapMiss.length;

			var totalHeapHit = new Array(length);
			var totalHeapMiss = new Array(length);
			var labels = new Array(length);

			for (var i=0; i<length; i++) {

				var date = new Date(responseTotalHeapHit[i][0]);

				labels[i] = date.toLocaleString();
				heapHits = responseTotalHeapHit[i][1];
				heapMisses = responseTotalHeapMiss[i][1];

				totalHeapHit[i] = heapHits;
				totalHeapMiss[i] = heapMisses;

				totalHeapHits += heapHits; 
				totalHeapMisses += heapMisses; 
			}

			cacheHAHeapHistoryChartData.labels = labels;
			cacheHAHeapHistoryChartData.datasets[0].data = totalHeapHit;
			cacheHAHeapHistoryChartData.datasets[1].data = totalHeapMiss;

			cacheHAHeapHistoryChart.createChart("line", cacheHAHeapHistoryChartOptions, cacheHAHeapHistoryChartData);

			if (cacheHAHeapChart != null) {

				cacheHAHeapChartData[0].value = totalHeapHits;
				cacheHAHeapChartData[1].value = totalHeapMisses;

				cacheHAHeapChart.createChart("pie", cacheHAHeapChartOptions, cacheHAHeapChartData);
			}
		}
		
		// Accesos a disco.
		if (cacheHADiskHistoryChart != null) {
			
			var totalDiskHits = 0;
			var totalDiskMisses = 0;
			
			var responseTotalDiskHit = jsonResponse["data"]["TotalDiskHit"];
			var responseTotalDiskMiss = jsonResponse["data"]["TotalDiskMiss"];
			
			var length = responseTotalDiskHit.length < responseTotalDiskMiss.length ? responseTotalDiskHit.length : responseTotalDiskMiss.length;
			
			var totalDiskHit = new Array(length);
			var totalDiskMiss = new Array(length);
			var labels = new Array(length);
			
			for (var i=0; i<length; i++) {
				
				var date = new Date(responseTotalDiskHit[i][0]);
				
				labels[i] = date.toLocaleString();
				diskHits = responseTotalDiskHit[i][1];
				diskMisses = responseTotalDiskMiss[i][1];
				
				totalDiskHit[i] = diskHits;
				totalDiskMiss[i] = diskMisses;
				
				totalDiskHits += diskHits; 
				totalDiskMisses += diskMisses; 
			}
			
			cacheHADiskHistoryChartData.labels = labels;
			cacheHADiskHistoryChartData.datasets[0].data = totalDiskHit;
			cacheHADiskHistoryChartData.datasets[1].data = totalDiskMiss;
			
			cacheHADiskHistoryChart.createChart("line", cacheHADiskHistoryChartOptions, cacheHADiskHistoryChartData);
			
			if (cacheHADiskChart != null) {
				
				cacheHADiskChartData[0].value = totalDiskHits;
				cacheHADiskChartData[1].value = totalDiskMisses;
				
				cacheHADiskChart.createChart("pie", cacheHADiskChartOptions, cacheHADiskChartData);
			}
		}
		
		// Uso de memoria.
		if (cacheHeapHistoryChart != null) {
			
			var responseTotalHeapSpace = jsonResponse["data"]["TotalHeapSpace"];
			
			var length = responseTotalHeapSpace.length;
			var totalHeapSpace = new Array(length);
			var labels = new Array(length);
			
			cacheHeapHistoryChartData.heapSpaceUseSummatory = 0;
			for (var i=0; i<length; i++) {
				
				var date = new Date(responseTotalHeapSpace[i][0]);
				var heapSpace = responseTotalHeapSpace[i][1];
				
				labels[i] = date.toLocaleString();
				totalHeapSpace[i] = heapSpace;
				
				cacheHeapHistoryChartData.heapSpaceUseSummatory += heapSpace; 
			}
			cacheHeapHistoryChartData.heapSpaceUseSamples = length; 
			
			cacheHeapHistoryChartData.labels = labels;
			cacheHeapHistoryChartData.datasets[0].data = totalHeapSpace;
			
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
			
			var responseTotalDiskSpace = jsonResponse["data"]["TotalDiskSpace"];
			
			var length = responseTotalDiskSpace.length;
			var totalDiskSpace = new Array(length);
			var labels = new Array(length);
			
			cacheDiskHistoryChartData.diskSpaceUseSummatory = 0;
			for (var i=0; i<length; i++) {
				
				var date = new Date(responseTotalDiskSpace[i][0]);
				var diskSpace = responseTotalDiskSpace[i][1];
				
				labels[i] = date.toLocaleString();
				totalDiskSpace[i] = diskSpace;
				
				cacheDiskHistoryChartData.diskSpaceUseSummatory += diskSpace; 
			}
			cacheDiskHistoryChartData.diskSpaceUseSamples = length; 
			
			cacheDiskHistoryChartData.labels = labels;
			cacheDiskHistoryChartData.datasets[0].data = totalDiskSpace;
			
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
	else {

		// Accesos a memoria.
		if (cacheHAHeapHistoryChart != null && jsonResponse["data"]["TotalHeapHit"].length > 0) {

			var data = [jsonResponse["data"]["TotalHeapHit"][0][1], jsonResponse["data"]["TotalHeapMiss"][0][1]];
			var date = new Date(jsonResponse["data"]["TotalHeapHit"][0][0]);
			var label = date.toLocaleString();

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
		if (cacheHADiskHistoryChart != null && jsonResponse["data"]["TotalDiskHit"].length > 0) {
			
			var data = [jsonResponse["data"]["TotalDiskHit"][0][1], jsonResponse["data"]["TotalDiskMiss"][0][1]];
			var date = new Date(jsonResponse["data"]["TotalDiskHit"][0][0]);
			var label = date.toLocaleString();
			
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
		if (cacheHeapHistoryChart != null && jsonResponse["data"]["TotalHeapSpace"].length > 0) {
			
			var data = [jsonResponse["data"]["TotalHeapSpace"][0][1]];
			var date = new Date(jsonResponse["data"]["TotalHeapSpace"][0][0]);
			var label = date.toLocaleString();
			
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
		if (cacheDiskHistoryChart != null && jsonResponse["data"]["TotalDiskSpace"].length > 0) {
			
			var data = [jsonResponse["data"]["TotalDiskSpace"][0][1]];
			var date = new Date(jsonResponse["data"]["TotalDiskSpace"][0][0]);
			var label = date.toLocaleString();
			
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
