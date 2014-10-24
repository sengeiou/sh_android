//Tamaño de heap de la caché, respecto a nu manager. 
var cacheHeapSizeChartOptions = clone(lineChartOptions);
cacheHeapSizeChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheHeapSizeChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cacheHeapSizeChartData = [
                              {
                            	  value: 10,
                            	  color:"rgba(220,220,220,1)",
                            	  highlight: "rgba(220,220,220,0.2)",
                            	  label: "Caché"
                              },
                              {
                            	  value: 50,
                            	  color: "rgba(151,187,205,1)",
                            	  highlight: "rgba(151,187,205,0.2)",
                            	  label: "Manager"
                              }
                              ];

//Tamaño de disco de la caché, respecto a su manager. 
var cacheDiskSizeChartOptions = clone(lineChartOptions);
cacheDiskSizeChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheDiskSizeChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cacheDiskSizeChartData = [
                              {
                            	  value: 10,
                            	  color:"rgba(220,220,220,1)",
                            	  highlight: "rgba(220,220,220,0.2)",
                            	  label: "Caché"
                              },
                              {
                            	  value: 50,
                            	  color: "rgba(151,187,205,1)",
                            	  highlight: "rgba(151,187,205,0.2)",
                            	  label: "Manager"
                              }
                              ];

/**
 * Pinta en pantalla los gráficos de las dimensiones relativas de esta caché,
 * respecto a su gestor.
 * 
 * 
 * @param cacheManager Nombre del gestor de cachés que contiene esta caché.
 * @param cache Nombre de la caché de la que se desean mostrar sus detalles.
 * @param cacheHeap Nombre del gráfico histórico de Hits y Adds a nivel de memoria.
 * @param cacheMaxBytesLocalHeap Tamaño en memoria definido para la caché.
 * @param managerMaxBytesLocalHeap Tamaño en memoria definido para el gestor de cachés.
 * @param cacheDisk Nombre del gráfico de Hits y Adds a nivel de memoria.
 * @param cacheMaxBytesLocalDisk Tamaño en disco definido para la caché.
 * @param managerMaxBytesLocalDisk Tamaño en disco definido para el gestor de cachés.
 */
function drawCacheSizes(cacheManager, cache,
		cacheHeap, cacheMaxBytesLocalHeap, managerMaxBytesLocalHeap,
		cacheDisk, cacheMaxBytesLocalDisk, managerMaxBytesLocalDisk) {

	// Heap
	var cacheHeapSizeChart = ChartHelper(cacheHeap);
	
	var cachePercent = (cacheMaxBytesLocalHeap * 100)/managerMaxBytesLocalHeap;
	cacheHeapSizeChartData[0].value = cacheMaxBytesLocalHeap;
	cacheHeapSizeChartData[0].label = cache + ' ' + Number(cachePercent).toFixed(2).toLocaleString() + '%';
	cacheHeapSizeChartData[1].value = managerMaxBytesLocalHeap;
	cacheHeapSizeChartData[1].label = cacheManager + ' ' + Number(100 - cachePercent).toFixed(2).toLocaleString() + '%';
	
	cacheHeapSizeChart.createChart("pie", cacheHeapSizeChartOptions, cacheHeapSizeChartData);
	
	// Disk
	var cacheDiskSizeChart = ChartHelper(cacheDisk);
	
	cachePercent = (cacheMaxBytesLocalDisk * 100)/managerMaxBytesLocalDisk;
	cacheDiskSizeChartData[0].value = cacheMaxBytesLocalDisk;
	cacheDiskSizeChartData[0].label = cache + ' ' + Number(cachePercent).toFixed(2).toLocaleString() + '%';
	cacheDiskSizeChartData[1].value = managerMaxBytesLocalDisk;
	cacheDiskSizeChartData[1].label = cacheManager + ' ' + Number(100 - cachePercent).toFixed(2).toLocaleString() + '%';
	
	cacheDiskSizeChart.createChart("pie", cacheDiskSizeChartOptions, cacheDiskSizeChartData);
}
