//Tamaño de heap de la caché, respecto a nu manager. 
var cachesHeapSizeChartOptions = clone(radarChartOptions);
cachesHeapSizeChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cachesHeapSizeChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cachesHeapSizeChartData = {

		labels: ["Eating", "Drinking", "Sleeping", "Designing", "Coding", "Cycling", "Running"],
		datasets: [
		           {
		        	   label: "My First dataset",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: [65, 59, 90, 81, 56, 55, 40]
		           },
		           {
		        	   label: "My Second dataset",
		        	   fillColor: "rgba(151,187,205,0.2)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,187,205,1)",
		        	   data: [28, 48, 40, 19, 96, 27, 100]
		           }
		           ]
};

//Tamaño de disco de la caché, respecto a su manager. 
var cachesDiskSizeChartOptions = clone(radarChartOptions);
cachesDiskSizeChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cachesDiskSizeChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cachesDiskSizeChartData = {
		labels: ["Eating", "Drinking", "Sleeping", "Designing", "Coding", "Cycling", "Running"],
		datasets: [
		           {
		        	   label: "My First dataset",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: [65, 59, 90, 81, 56, 55, 40]
		           },
		           {
		        	   label: "My Second dataset",
		        	   fillColor: "rgba(151,187,205,0.2)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,187,205,1)",
		        	   data: [28, 48, 40, 19, 96, 27, 100]
		           }
		           ]
};

/**
 * Pinta en pantalla los gráficos de las dimensiones relativas de esta caché,
 * respecto a su gestor.
 * 
 * 
 * @param cacheManager Nombre del gestor de cachés.
 * @param cachesHeapSize Nombre del gráfico histórico de Hits y Adds a nivel de memoria.
 * @param managerMaxBytesLocalHeap Tamaño en memoria definido para el gestor de cachés.
 * @param cacheDisk Nombre del gráfico de Hits y Adds a nivel de memoria.
 * @param managerMaxBytesLocalDisk Tamaño en disco definido para el gestor de cachés.
 */
function drawCachesSizeDistribution(cacheManager,
		cachesHeapSize, managerMaxBytesLocalHeap,
		cachesDiskSize, managerMaxBytesLocalDisk) {

	// Heap
	var cacheHeapSizeChart = ChartHelper(cachesHeapSize);

	cacheHeapSizeChart.createChart("radar", cachesHeapSizeChartOptions, cachesHeapSizeChartData);

	// Disk
	var cacheDiskSizeChart = ChartHelper(cachesDiskSize);

	cacheDiskSizeChart.createChart("radar", cachesDiskSizeChartOptions, cachesDiskSizeChartData);
}
