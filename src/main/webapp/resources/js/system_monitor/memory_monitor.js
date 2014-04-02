/**
 * Estructura global del monitor.
 */
var MemoryMonitor = {
		period : null,
		timeRange : null,
		memoryHistoryPlot : null,
		committedMemoryInstantPlot : null,
		usedMemoryInstantPlot : null
}


/**
 * Inicializa el monitor de Memoria.
 * 
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param period Periodo de refresco en segundos. 
 * @param timeRange Rango temporal a mostrar.
 * @param memoryHistoryPlotElement Nombre del elemento en el que se renderiza el histórico de distribución de la memoria.
 * @param committedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria reservada instantanea.
 * @param usedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria usada instantanea.
 */
function initMemoryMonitor(servicesURL, period, timeRange, memoryHistoryPlotElement, committedMemoryInstantPlotElement, usedMemoryInstantPlotElement) {

	$.jqplot.config.enablePlugins = true;
	MemoryMonitor.period = period;
	MemoryMonitor.timeRange = timeRange;

	if (memoryHistoryPlotElement) {
		MemoryMonitor.memoryHistoryPlot = createMemoryHistoryPlot(servicesURL, memoryHistoryPlotElement);
	}

	if (committedMemoryInstantPlotElement) {
		MemoryMonitor.committedMemoryInstantPlot = createCommittedMemoryInstantPlot(servicesURL, committedMemoryInstantPlotElement);
	}

	if (usedMemoryInstantPlotElement) {
		MemoryMonitor.usedMemoryInstantPlot = createUsedMemoryInstantPlot(servicesURL, usedMemoryInstantPlotElement);
	}

	stopMemoryMonitor();
}

/**
 * Alimentador de información de la gráfica de distribución de memoria en el tiempo.
 */
function memoryHistoryDataRenderer(url, plot, options) {

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", options["url"], false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({period: MemoryMonitor.period, timeRange: MemoryMonitor.timeRange}));

	var jsonResponse = JSON.parse(xhr.responseText);

	ret = [
	       jsonResponse["data"]["TotalMaxMemory"],
	       jsonResponse["data"]["TotalInitMemory"],
	       jsonResponse["data"]["TotalCommitted"],
	       jsonResponse["data"]["TotalUsedMemory"],
	       jsonResponse["data"]["UsedHeapMemory"],
	       jsonResponse["data"]["UsedNonHeapMemory"]
	       ];

	return ret;
}

/**
 * Crea la gráfica de distribución de memoria en el tiempo.
 * 
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param plotElement Nombre del elemento en el que se renderiza el histórico de distribución de la memoria.
 * 
 * @returns una referencia a la gráfica de distribución de memoria en el tiempo.
 */
function createMemoryHistoryPlot(servicesURL, plotElement) {

	return $.jqplot(plotElement, [[0], [0], [0], [0], [0], [0]], 
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
			looseZoom: true,
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
		        { color: '#ff1111', linePattern: 'dashed' },
		        { color: '#00b3ff', linePattern: 'dashed' },
		        { color: '#90d91d' },
		        { color: '#fcc226' },
		        { color: '#cffe2e' },
		        { color: '#9ab66e' }
		        ],
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
		        			formatter: $.jqplot.BytesTickFormatter,
		        			formatString:'%d',
		        			fontSize: '10pt'
		        		},
		        		min: 0
		        	}
		        },
		        dataRenderer: memoryHistoryDataRenderer,
		        dataRendererOptions: {
		        	url: servicesURL + '/system/memory'
		        },
		        legend: {
		        	renderer: jQuery.jqplot.EnhancedLegendRenderer,
		        	labels: [
		        	         'M&aacute;xima',
		        	         'Inicial', 
		        	         'Reservada',
		        	         'Total Usada',
		        	         'Heap',
		        	         'NonHeap'
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
 * Alimentador de información de la gráfica de distribución de memoria dipsonible instantanea.
 */
function committedMemoryInstantDataRenderer(url, plot, options) {

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", options["url"], false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({period:null, timeRange:null}));

	var jsonResponse = JSON.parse(xhr.responseText);

	var committedMemory = parseFloat(jsonResponse["data"]["TotalCommitted"], 10); 
	var freeMemory = parseFloat(jsonResponse["data"]["TotalMaxMemory"], 10)-committedMemory;

	ret = [[committedMemory], [freeMemory]];

	return ret;
}

/**
 * Crea la gráfica de memoria reservada.
 * 
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param plotElement Nombre del elemento en el que se renderiza la memoria reservada instantanea.
 * 
 * @returns una referencia a la gráfica de memoria reservada.
 */
function createCommittedMemoryInstantPlot(servicesURL, plotElement) {

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
		        { color: '#90d91d', showMarker: false },
		        { color: '#c5e3f3', showMarker: false }
		        ],
		        axes: {
		        	xaxis: {
		        		renderer: $.jqplot.CategoryAxisRenderer,
		        		ticks: ["Disponible"],
		        		tickOptions: {
		        			fontSize: '10pt'
		        		}
		        	},
		        	yaxis: {
		        		tickOptions: {
		        			formatter: $.jqplot.BytesTickFormatter,
		        			formatString:'%d',
		        			fontSize: '10pt'
		        		},
		        		padMin: 0,
		        		min: 0
		        	}
		        },
		        legend: {
		        	show: false,
		        },
		        dataRenderer: committedMemoryInstantDataRenderer,
		        dataRendererOptions: {
		        	url: servicesURL + '/system/memory'
		        }
	});
}

/**
 * Alimentador de información de la gráfica de distribución de memoria usada instantanea.
 */
function usedMemoryInstantDataRenderer(url, plot, options) {

	var ret = null;
	var xhr = new XMLHttpRequest();

	xhr.open("POST", options["url"], false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({period:null, timeRange:null}));

	var jsonResponse = JSON.parse(xhr.responseText);

	var committedMemory = parseFloat(jsonResponse["data"]["TotalCommitted"], 10); 
	var usedHeapMemory = parseFloat(jsonResponse["data"]["UsedHeapMemory"], 10); 
	var usedNonHeapMemory = parseFloat(jsonResponse["data"]["UsedNonHeapMemory"], 10); 
	var freeMemory = committedMemory - usedHeapMemory - usedNonHeapMemory;

	ret = [[usedNonHeapMemory], [usedHeapMemory], [freeMemory]];

	return ret;
}

/**
 * Crea la gráfica de memoria usada.
 * 
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param plotElement Nombre del elemento en el que se renderiza la memoria usada instantanea.
 * 
 * @returns una referencia a la gráfica de memoria usada.
 */
function createUsedMemoryInstantPlot(servicesURL, plotElement) {

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
		        { color: '#cffe2e', showMarker: false },
		        { color: '#9ab66e', showMarker: false },
		        { color: '#c5e3f3', showMarker: false }
		        ],
		        axes: {
		        	xaxis: {
		        		renderer: $.jqplot.CategoryAxisRenderer,
		        		ticks: ["Usada"],
		        		tickOptions: {
		        			fontSize: '10pt'
		        		}
		        	},
		        	yaxis: {
		        		tickOptions: {
		        			formatter: $.jqplot.BytesTickFormatter,
		        			formatString:'%d',
		        			fontSize: '10pt'
		        		},
		        		padMin: 0,
		        		min: 0
		        	}
		        },
		        legend: {
		        	show: false
		        },
		        dataRenderer: usedMemoryInstantDataRenderer,
		        dataRendererOptions: {
		        	url: servicesURL + '/system/memory'
		        }
	});
}

/**
 * Detiene/Reanuda el monitor de memoria en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor de memoria.
 */
function freezeMemoryMonitor(freeze) {
	if (freeze) {
		stopMemoryMonitor();
	} else {
		startMemoryMonitor();
	}
}

/**
 * Detiene el monitor de memoria.
 */
function stopMemoryMonitor() {
	if (this.memoryMonitorInterval) {
		clearInterval(this.memoryMonitorInterval);
		this.memoryMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de memoria.
 */
function startMemoryMonitor() {
	stopMemoryMonitor();
	this.memoryMonitorInterval = setInterval(function() {

		// Historial de memoria.
		if (MemoryMonitor.memoryHistoryPlot) {
			MemoryMonitor.memoryHistoryPlot.replot({ data: [] });
		}

		// Instante de memoria disponible.
		if (MemoryMonitor.committedMemoryInstantPlot) {
			MemoryMonitor.committedMemoryInstantPlot.replot({ data: [] });
		}

		// Instante de memoria usada.
		if (MemoryMonitor.usedMemoryInstantPlot) {
			MemoryMonitor.usedMemoryInstantPlot.replot({ data: [] });
		}

	}, 1000);
}
