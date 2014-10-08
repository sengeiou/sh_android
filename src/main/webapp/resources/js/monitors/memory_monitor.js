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
 * @param period Periodo de refresco en segundos. 
 * @param timeRange Rango temporal a mostrar.
 * @param memoryHistoryPlotElement Nombre del elemento en el que se renderiza el histórico de distribución de la memoria.
 * @param committedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria reservada instantanea.
 * @param usedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria usada instantanea.
 */
function initMemoryMonitor(period, timeRange, memoryHistoryPlotElement, committedMemoryInstantPlotElement, usedMemoryInstantPlotElement) {

	$.jqplot.config.enablePlugins = true;
	MemoryMonitor.period = period;
	MemoryMonitor.timeRange = timeRange;

	if (memoryHistoryPlotElement) {
		MemoryMonitor.memoryHistoryPlot = createMemoryHistoryPlot(memoryHistoryPlotElement);
	}

	if (committedMemoryInstantPlotElement) {
		MemoryMonitor.committedMemoryInstantPlot = createCommittedMemoryInstantPlot(committedMemoryInstantPlotElement);
	}

	if (usedMemoryInstantPlotElement) {
		MemoryMonitor.usedMemoryInstantPlot = createUsedMemoryInstantPlot(usedMemoryInstantPlotElement);
	}

	stopMemoryHistoryMonitor();
	stopMemoryInstantMonitor();
}

/**
 * Alimentador de información de la gráfica de distribución de memoria en el tiempo.
 * 
 * @param data Conjunto de datos actual representado en la gráfica.
 * @param plot Gràfica a la que está asociada esta función de renderizado de datos.
 * @param options Opciones indicadas en la gráfica para ser usadas desde esta función de renderizado.
 */
function memoryHistoryDataRenderer(data, plot, options) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	if (plot && plot.noDataIndicator) {

		plot.noDataIndicator.show = false;
	}

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
 * @param plotElement Nombre del elemento en el que se renderiza el histórico de distribución de la memoria.
 * 
 * @returns una referencia a la gráfica de distribución de memoria en el tiempo.
 */
function createMemoryHistoryPlot(plotElement) {

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
		        noDataIndicator: {
		        	show: true,
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
		        			formatter: $.jqplot.BytesTickFormatter,
		        			formatString:'%d',
		        			fontSize: '10pt'
		        		},
		        		min: 0
		        	}
		        },
		        dataRenderer: memoryHistoryDataRenderer,
		        dataRendererOptions: {
		        	url: App.servicesURL + '/system/memory'
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
 * 
 * @param data Conjunto de datos actual representado en la gráfica.
 * @param plot Gràfica a la que está asociada esta función de renderizado de datos.
 * @param options Opciones indicadas en la gráfica para ser usadas desde esta función de renderizado.
 */
function committedMemoryInstantDataRenderer(data, plot, options) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	if (plot && plot.noDataIndicator) {

		plot.noDataIndicator.show = false;
	}

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
 * @param plotElement Nombre del elemento en el que se renderiza la memoria reservada instantanea.
 * 
 * @returns una referencia a la gráfica de memoria reservada.
 */
function createCommittedMemoryInstantPlot(plotElement) {

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
		        noDataIndicator: {
		        	show: true,
		        	// Here, an animated gif image is rendered with some loading text.
		        	indicator: '<img src="' + App.imagesURL + '/ajax-loader.gif" /><br />Esperando datos...'
		        },
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
		        	url: App.servicesURL + '/system/memory'
		        }
	});
}

/**
 * Alimentador de información de la gráfica de distribución de memoria usada instantanea.
 * 
 * @param data Conjunto de datos actual representado en la gráfica.
 * @param plot Gràfica a la que está asociada esta función de renderizado de datos.
 * @param options Opciones indicadas en la gráfica para ser usadas desde esta función de renderizado.
 */
function usedMemoryInstantDataRenderer(data, plot, options) {

	delete data;

	var ret = null;
	var xhr = new XMLHttpRequest();

	if (plot && plot.noDataIndicator) {

		plot.noDataIndicator.show = false;
	}

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
 * @param plotElement Nombre del elemento en el que se renderiza la memoria usada instantanea.
 * 
 * @returns una referencia a la gráfica de memoria usada.
 */
function createUsedMemoryInstantPlot(plotElement) {

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
		        noDataIndicator: {
		        	show: true,
		        	// Here, an animated gif image is rendered with some loading text.
		        	indicator: '<img src="' + App.imagesURL + '/ajax-loader.gif" /><br />Esperando datos...'
		        },
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
		        	url: App.servicesURL + '/system/memory'
		        }
	});
}

/**
 * Detiene/Reanuda el monitor del histórico de memoria en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor del histórico de memoria.
 */
function freezeMemoryHistoryMonitor(freeze) {
	if (freeze) {
		stopMemoryHistoryMonitor();
	} else {
		startMemoryHistoryMonitor();
	}
}

/**
 * Detiene/Reanuda el monitor de memoria en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor de memoria.
 */
function freezeMemoryInstantMonitor(freeze) {
	if (freeze) {
		stopMemoryInstantMonitor();
	} else {
		startMemoryInstantMonitor();
	}
}

/**
 * Detiene el monitor del histórico de memoria.
 */
function stopMemoryHistoryMonitor() {
	if (App.memoryHistoryMonitorInterval) {
		clearInterval(App.memoryHistoryMonitorInterval);
		App.memoryHistoryMonitorInterval = null;
	}
}

/**
 * Detiene el monitor de memoria.
 */
function stopMemoryInstantMonitor() {
	if (App.memoryInstantMonitorInterval) {
		clearInterval(App.memoryInstantMonitorInterval);
		App.memoryInstantMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor del histórico de memoria.
 */
function startMemoryHistoryMonitor() {
	stopMemoryHistoryMonitor();
	App.memoryHistoryMonitorInterval = setInterval(function() {

		// Historial de memoria.
		if (MemoryMonitor.memoryHistoryPlot) {
			MemoryMonitor.memoryHistoryPlot.replot({ data: [] });
		}

	}, 1000);
}

/**
 * Reanuda el monitor de memoria.
 */
function startMemoryInstantMonitor() {
	stopMemoryInstantMonitor();
	App.memoryInstantMonitorInterval = setInterval(function() {
		
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
