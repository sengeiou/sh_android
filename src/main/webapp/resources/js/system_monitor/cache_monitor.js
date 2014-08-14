/**
 * Estructura global del monitor.
 */
var CacheMonitor = {
		period : null,
		timeRangeStart : null,
		timeRangeEnd : null,

		cacheHeapHFHistoryChart : null,
		cacheHeapHFInstantChart : null,
		cacheDiskHFHistoryChart : null,
		cacheDiskHFInstantChart : null,
		
		cacheHeapHistoryChart : null,
		cacheHeapInstantChart : null,
		cacheDiskHistoryChart : null,
		cacheDiskInstantChart : null
}

/**
 * Inicializa el monitor de Caché.
 * 
 * @param period Periodo de refresco en segundos. 
 * @param timeRangeStart Inicio del rango temporal a mostrar.
 * @param timeRangeEnd Fin del rango temporal a mostrar.
 * @param cacheHeapHFHistoryChart Nombre del elemento en el que se renderiza el histórico de hits y fails en el heap de la caché.
 * @param cacheHeapHFInstantChart Nombre del elemento en el que se renderiza la proporción de hits vs fails en este instante en el heap de la caché.
 * @param cacheDiskHFHistoryChart Nombre del elemento en el que se renderiza el histórico de hits y fails en el área de disco de la caché.
 * @param cacheDiskHFInstantChart Nombre del elemento en el que se renderiza la proporción de hits vs fails en este instante en el área de disco de la caché.
 * @param cacheHeapHistoryChart Nombre del elemento en el que se renderiza el histórico de uso de memoria de la caché.
 * @param cacheHeapInstantChart Nombre del elemento en el que se renderiza el uso de memoria de la caché en este instante.
 * @param cacheDiskHistoryChart Nombre del elemento en el que se renderiza el histórico de uso de disco de la caché.
 * @param cacheDiskInstantChart Nombre del elemento en el que se renderiza el uso de disco de la caché en este instante.
 */
function initCacheMonitor(period, timeRangeStart, timeRangeEnd, 
		cacheHeapHFHistoryChart, cacheHeapHFInstantChart,
		cacheDiskHFHistoryChart, cacheDiskHFInstantChart,
		cacheHeapHistoryChart, cacheHeapInstantChart, 
		cacheDiskHistoryChart, cacheDiskInstantChart) {

	
	//cacheHeapHistory.addData([40, 60], "August"); // Añade un dato al final.
	//cacheHeapHistory.removeData(); // Borra el primer dato de todas las series.

	//cacheHeapHistory.datasets[0].points[2].value = 50;
	//cacheHeapHistory.update(); // Repinta los datos cargados en el array;
	
	
	CacheMonitor.period = period;
	CacheMonitor.timeRangeStart = timeRangeStart;
	CacheMonitor.timeRangeEnd = timeRangeEnd;
	CacheMonitor.cacheHeapHFHistoryChart = cacheHeapHFHistoryChart;
	CacheMonitor.cacheHeapHFInstantChart = cacheHeapHFInstantChart;
	CacheMonitor.cacheDiskHFHistoryChart = cacheDiskHFHistoryChart;
	CacheMonitor.cacheDiskHFInstantChart = cacheDiskHFInstantChart;
	CacheMonitor.cacheHeapHistoryChart = cacheHeapHistoryChart;
	CacheMonitor.cacheHeapInstantChart = cacheHeapInstantChart;
	CacheMonitor.cacheDiskHistoryChart = cacheDiskHistoryChart;
	CacheMonitor.cacheDiskInstantChart = cacheDiskInstantChart;

	if (cpuHistoryPlotElement) {
		CPUMonitor.cpuHistoryPlot = createCPUHistoryPlot(cpuHistoryPlotElement);
	}

	if (cpuLoadInstantPlotElement) {
		CPUMonitor.cpuLoadInstantPlot = createCPULoadInstantPlot(cpuLoadInstantPlotElement);
	}

	stopCPUHistoryMonitor();
	stopCPUInstantMonitor();
}

/**
 * Alimentador de información de la gráfica de actividad del procesador en el tiempo.
 * 
 * @param data Conjunto de datos actual representado en la gráfica.
 * @param plot Gràfica a la que está asociada esta función de renderizado de datos.
 * @param options Opciones indicadas en la gráfica para ser usadas desde esta función de renderizado.
 */
function cpuHistoryDataRenderer(data, plot, options) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	if (plot && plot.noDataIndicator) {

		plot.noDataIndicator.show = false;
	}

	xhr.open("POST", options["url"], false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({period: CPUMonitor.period, timeRange: CPUMonitor.timeRange}));

	var jsonResponse = JSON.parse(xhr.responseText);

	ret = [
	       jsonResponse["data"]["SystemCPULoad"],
	       jsonResponse["data"]["ApplicationCPULoad"],
	       jsonResponse["data"]["CPULoad"]
	       ];
	return ret;
}

/**
 * Crea la gráfica de actividad del procesador en el tiempo.
 * 
 * @param plotElement Nombre del elemento en el que se renderiza el histórico de actividad de la cpu.
 * 
 * @returns una referencia a la gráfica de actividad del procesador.
 */
function createCPUHistoryPlot(plotElement) {

	return $.jqplot(plotElement, [], 
			{
		grid: {
			drawBorder: true,
			drawGridlines: true,
			background: '#ffffff',
			shadow: true
		},
		axesDefaults: {
		},
		animate: true,
		animateReplot: false,
		cursor: {
			zoom: true,
			looseZoom: false,
			showTooltip: true
		},
		seriesDefaults:{
			rendererOptions: {
				smooth: true
			},
			linePattern: 'solid',
			lineWidth: 1,
			showMarker: false,
			shadow: false
		},
		series:[
		        { color: '#fcc226' },
		        { color: '#cffe2e' },
		        { color: '#9ab66e', linePattern: 'dashed' }
		        ],
		        noDataIndicator: {
		        	show: true,
		        	// Here, an animated gif image is rendered with some loading text.
		        	indicator: '<img src="' + App.imagesURL + '/ajax-loader.gif" /><br />Esperando datos...'
		        },
		        axes:{
		        	xaxis:{
		        		renderer: $.jqplot.DateAxisRenderer,
		        		tickRenderer: $.jqplot.CanvasAxisTickRenderer,
		        		tickOptions: {
		        			formatString:'%b %e\n%H:%M',
		        			angle: -30,
		        			fontSize: '10pt'
		        		},
		        		tickInterval:'1 minutes'
		        	},
		        	yaxis:{
		        		tickOptions: {
		        			formatString:'%.2f\%',
		        			fontSize: '10pt'
		        		},
		        		min: 0
		        		//max: 100
		        	}
		        },
		        dataRenderer: cpuHistoryDataRenderer,
		        dataRendererOptions: {
		        	url: App.servicesURL + '/system/cpu'
		        },
		        legend: {
		        	renderer: jQuery.jqplot.EnhancedLegendRenderer,
		        	labels: [
		        	         'Sistema',
		        	         'Aplicaci&oacute;n',
		        	         'Carga total'
		        	         ],
		        	         show: true,
		        	         showLabels: true,
		        	         showSwatches: true,
		        	         rowSpacing: '10px',
		        	         marginLeft: '10px',
		        	         placement: 'outsideGrid',
		        	         location: 'e',
		        	         border: 'none'
		        }
			});
}

/**
 * Alimentador de información de la gráfica de la carga instantanea del procesador.
 * 
 * @param data Conjunto de datos actual representado en la gráfica.
 * @param plot Gràfica a la que está asociada esta función de renderizado de datos.
 * @param options Opciones indicadas en la gráfica para ser usadas desde esta función de renderizado.
 */
function cpuLoadInstantDataRenderer(data, plot, options) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	if (plot && plot.noDataIndicator) {

		plot.noDataIndicator.show = false;
	}

	xhr.open("POST", options["url"], false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({period: null, timeRange: null}));

	var jsonResponse = JSON.parse(xhr.responseText);

	var systemCPULoad = parseFloat(jsonResponse["data"]["SystemCPULoad"], 10); 
	var applicationCPULoad = parseFloat(jsonResponse["data"]["ApplicationCPULoad"], 10); 

	ret = [
	       [systemCPULoad],
	       [applicationCPULoad]
	       ];

	return ret;
}

/**
 * Crea la gráfica de actividad de la carga instantanea de la cpu.
 * 
 * @param plotElement Nombre del elemento en el que se renderiza la carga instantanea de la cpu.
 * 
 * @returns una referencia a la gráfica de actividad de la carga instantanea de la cpu.
 */
function createCPULoadInstantPlot(plotElement) {

	return jQuery.jqplot (plotElement, [], {
		grid: {
			drawBorder: true,
			drawGridlines: true,
			background: '#ffffff',
			shadow: true
		},
		stackSeries: true,
		captureRightClick: true,
		seriesDefaults:{
			renderer:$.jqplot.BarRenderer,
			rendererOptions: {
				barMargin: 30,
				highlightMouseDown: true   
			},
			pointLabels: {show: true}
		},
		animate: true,
		animateReplot: false,
		series:[
		        { color: '#fcc226', showMarker: false },
		        { color: '#cffe2e', showMarker: false }
		        ],
		        noDataIndicator: {
		        	show: true,
		        	// Here, an animated gif image is rendered with some loading text.
		        	indicator: '<img src="' + App.imagesURL + '/ajax-loader.gif" /><br />Esperando datos...'
		        },
		        axes: {
		        	xaxis: {
		        		renderer: $.jqplot.CategoryAxisRenderer,
		        		ticks: ["Carga"],
		        		tickOptions: {
		        			fontSize: '10pt'
		        		}
		        	},
		        	yaxis:{
		        		tickOptions: {
		        			formatString:'%.2f\%',
		        			fontSize: '10pt'
		        		},
		        		min: 0
		        		//max: 100
		        	}
		        },
		        legend: {
		        	show: false
		        },
		        dataRenderer: cpuLoadInstantDataRenderer,
		        dataRendererOptions: {
		        	url: App.servicesURL + '/system/cpu'
		        }
	});
}

/**
 * Detiene/Reanuda el monitor del histórico de CPU en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor del histórico de CPU.
 */
function freezeCPUHistoryMonitor(freeze) {

	if (freeze) {
		stopCPUHistoryMonitor();
	} else {
		startCPUHistoryMonitor();
	}
}

/**
 * Detiene/Reanuda el monitor de CPU en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor de CPU.
 */
function freezeCPUInstantMonitor(freeze) {
	
	if (freeze) {
		stopCPUInstantMonitor();
	} else {
		startCPUInstantMonitor();
	}
}

/**
 * Detiene el monitor del histórico de CPU.
 */
function stopCPUHistoryMonitor() {

	if (App.cpuHistoryMonitorInterval) {
		clearInterval(App.cpuHistoryMonitorInterval);
		App.cpuHistoryMonitorInterval = null;
	}
}

/**
 * Detiene el monitor de CPU.
 */
function stopCPUInstantMonitor() {
	
	if (App.cpuInstantMonitorInterval) {
		clearInterval(App.cpuInstantMonitorInterval);
		App.cpuInstantMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor del histórico de CPU.
 */
function startCPUHistoryMonitor() {
	
	stopCPUHistoryMonitor();
	App.cpuHistoryMonitorInterval = setInterval(function() { 		
		
		// Historial de actividad de la CPU.
		if (CPUMonitor.cpuHistoryPlot) {
			CPUMonitor.cpuHistoryPlot.replot({ data: [] });
		}
		
	}, 1000);
}

/**
 * Reanuda el monitor de CPU.
 */
function startCPUInstantMonitor() {
	
	stopCPUInstantMonitor();
	App.cpuInstantMonitorInterval = setInterval(function() { 		
		
		// Carga instantanea de la CPU.
		if (CPUMonitor.cpuLoadInstantPlot) {
			CPUMonitor.cpuLoadInstantPlot.replot({ data: [] });
		}
		
	}, 1000);
}

