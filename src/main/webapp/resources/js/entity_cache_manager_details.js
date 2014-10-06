//Tamaño de heap de la caché, respecto a nu manager. 
var cachesHeapDiskChartOptions = clone(radarChartOptions);
cachesHeapDiskChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cachesHeapDiskChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cachesHeapDiskChartData = {

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
var cachesHitAddChartOptions = clone(radarChartOptions);
cachesHitAddChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cachesHitAddChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cachesHitAddChartData = {
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
 * Pinta en pantalla los gráficos de uso relativo de las cachés, respecto a su gestor.
 * 
 * @param cacheManager Nombre del gestor de cachés.
 * @param cachesHeapDisk Nombre del gráfico de distribución de uso a nivel de memoria y disco.
 * @param managerMaxBytesLocalHeap Tamaño en memoria definido para el gestor de cachés.
 * @param managerMaxBytesLocalDisk Tamaño en disco definido para el gestor de cachés.
 * @param cachesHitAdd Nombre del gráfico de distribución de efectividad.
 */
function drawCachesUseDistribution(cacheManager,
		cachesHeapDisk, managerMaxBytesLocalHeap, managerMaxBytesLocalDisk,
		cachesHitAdd) {

	// HeapDisk
	var cachesHeapDiskChart = ChartHelper(cachesHeapDisk);

	cachesHeapDiskChart.createChart("radar", cachesHeapDiskChartOptions, cachesHeapDiskChartData);

	// HitAdd
	var cacheHitAddChart = ChartHelper(cachesHitAdd);

	cacheHitAddChart.createChart("radar", cachesHitAddChartOptions, cachesHitAddChartData);
}
