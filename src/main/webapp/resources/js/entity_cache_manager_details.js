//Tamaño de heap de la caché, respecto a nu manager. 
var cachesHeapDiskChartOptions = clone(radarChartOptions);
cachesHeapDiskChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= Math.floor(value).toFixed(2).toLocaleString() + '%' %>";
cachesHeapDiskChartOptions.multiTooltipTemplate = "<%= Math.floor(value).toFixed(2).toLocaleString() +'%' %>";

var cachesHeapDiskChartData = {

		labels: [],
		datasets: [
		           {
		        	   label: "Memoria",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           },
		           {
		        	   label: "Disco",
		        	   fillColor: "rgba(151,187,205,0.2)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,187,205,1)",
		        	   data: []
		           }
		           ]
};

//Tamaño de disco de la caché, respecto a su manager. 
var cachesHitAddChartOptions = clone(radarChartOptions);
cachesHitAddChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= Math.floor(value).toFixed(2).toLocaleString() + '%' %>";
cachesHitAddChartOptions.multiTooltipTemplate = "<%= Math.floor(value).toFixed(2).toLocaleString() +'%' %>";

var cachesHitAddChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Hit ratio",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           }
		           ]
};

/**
 * Retorna la información del gestor de cachés indicado.
 * 
 * @param cacheManager Nombre del gestor de cachés del que se desea obtener la información.
 * 
 * @returns la información del gestor de cachés indicado.
 */
function getCacheManager(cacheManager) {

	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + "/cache/retrieve/cacheManager", false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	// Envío.
	var requestObject = {
			alias: "CACHEMANAGER_DETAILS",
			status: {
				code: "OK",
				message: null
			},
			req: [0, 0, 0, 0, 0],
			name: cacheManager
	};

	xhr.send(JSON.stringify(requestObject));

	// Lectura de la respuesta.
	return JSON.parse(xhr.responseText);
}

/**
 * Retorna la información de monitorización de la caché indicada, más reciente.
 * 
 * @param cacheManager Gestor de caché al que pertenece la caché de la que se solicita la información.
 * @param cache Caché de la que se solicita la información.
 * 
 * @returns la información de monitorización de la caché indicada, más reciente.
 */
function getCacheInfo(cacheManager, cache) {

	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + "/system/cache", false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");

	// Envío.
	var requestObject = {
			period: null,
			offset: null,
			timeRange: null,
			cacheManager: cacheManager,
			cache: cache
	};
	xhr.send(JSON.stringify(requestObject));

	// Lectura de la respuesta.
	return JSON.parse(xhr.responseText)["data"];
}

/**
 * Pinta en pantalla los gráficos de uso relativo de las cachés, respecto a su gestor.
 * 
 * @param cacheManagerName Nombre del gestor de cachés.
 * @param cachesHeapDisk Nombre del gráfico de distribución de uso a nivel de memoria y disco.
 * @param cachesHitAdd Nombre del gráfico de distribución de efectividad.
 */
function drawCachesUseDistribution(cacheManagerName, cachesHeapDisk, cachesHitAdd) {

	// Obtención de la información del gestor de cachés.
	var cacheManager = getCacheManager(cacheManagerName);

	var maxBytesLocalHeap = cacheManager.maxBytesLocalHeap;
	var maxBytesLocalDisk = cacheManager.maxBytesLocalDisk;
	var caches = cacheManager.caches;

	if (caches != null) {

		var labels = new Array(caches.length);
		
		cachesHeapDiskChartData.labels = labels;
		cachesHeapDiskChartData.datasets[0].data = new Array(caches.length);
		cachesHeapDiskChartData.datasets[1].data = new Array(caches.length);
		
		cachesHitAddChartData.labels = labels;
		cachesHitAddChartData.datasets[0].data = new Array(caches.length);
		
		for(var i=0; i < caches.length; i++) {
			
			var cache = caches[i];
			
			labels[i] = cache.entityAlias;
			
			var cacheInfo = getCacheInfo(cacheManagerName, cache.entityAlias);
		
			// Kiviat de tamaño.
			var usedHeapSpace = cacheInfo["UsedHeapSpace"][0];
			var usedDiskSpace = cacheInfo["UsedDiskSpace"][0];
			
			cachesHeapDiskChartData.datasets[0].data[i] = cache.maxBytesLocalHeap == 0 ? 0 : (usedHeapSpace / cache.maxBytesLocalHeap) * 100;
			cachesHeapDiskChartData.datasets[1].data[i] = cache.maxBytesLocalDisk == 0 ? 0 : (usedDiskSpace / cache.maxBytesLocalDisk) * 100;

			// Kiviat de aciertos y fallos de caché.
			var totalHits = cacheInfo["TotalHeapHit"][0] + cacheInfo["TotalDiskHit"][0];
			var totalMiss = cacheInfo["TotalHeapMiss"][0] + cacheInfo["TotalDiskMiss"][0];
			var totalOperations = totalHits + totalMiss;

			cachesHitAddChartData.datasets[0].data[i] = totalOperations == 0 ? 0 : (totalHits / totalOperations) * 100;
		}

		// HeapDisk
		var cachesHeapDiskChart = ChartHelper(cachesHeapDisk);

		cachesHeapDiskChart.createChart("radar", cachesHeapDiskChartOptions, cachesHeapDiskChartData);

		// HitAdd
		var cacheHitAddChart = ChartHelper(cachesHitAdd);

		cacheHitAddChart.createChart("radar", cachesHitAddChartOptions, cachesHitAddChartData);
	}
}
