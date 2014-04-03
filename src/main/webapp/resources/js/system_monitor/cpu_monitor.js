/**
 * Estructura global del monitor.
 */
var CPUMonitor = {
		period : null,
		timeRange : null,
		cpuHistoryPlot : null,
		cpuLoadInstantPlot : null
}

/**
 * Inicializa el monitor de CPU.
 * 
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param period Periodo de refresco en segundos. 
 * @param timeRange Rango temporal a mostrar.
 * @param cpuHistoryPlotElement Nombre del elemento en el que se renderiza el histórico de actividad de la cpu.
 * @param cpuLoadInstantPlotElement Nombre del elemento en el que se renderiza la carga instantanea de la cpu.
 */
function initCPUMonitor(servicesURL, period, timeRange, cpuHistoryPlotElement, cpuLoadInstantPlotElement) {

	$.jqplot.config.enablePlugins = true;
	CPUMonitor.period = period;
	CPUMonitor.timeRange = timeRange;

	if (cpuHistoryPlotElement) {
		CPUMonitor.cpuHistoryPlot = createCPUHistoryPlot(servicesURL, cpuHistoryPlotElement);
	}

	if (cpuLoadInstantPlotElement) {
		CPUMonitor.cpuLoadInstantPlot = createCPULoadInstantPlot(servicesURL, cpuLoadInstantPlotElement);
	}

	stopCPUMonitor();
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
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param plotElement Nombre del elemento en el que se renderiza el histórico de actividad de la cpu.
 * 
 * @returns una referencia a la gráfica de actividad del procesador.
 */
function createCPUHistoryPlot(servicesURL, plotElement) {

	return $.jqplot(plotElement, [[0], [0], [0]], 
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
		        	url: servicesURL + '/system/cpu'
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
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * @param plotElement Nombre del elemento en el que se renderiza la carga instantanea de la cpu.
 * 
 * @returns una referencia a la gráfica de actividad de la carga instantanea de la cpu.
 */
function createCPULoadInstantPlot(servicesURL, plotElement) {

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
		        	url: servicesURL + '/system/cpu'
		        }
	});
}

/**
 * Detiene/Reanuda el monitor de CPU en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor de CPU.
 */
function freezeCPUMonitor(freeze) {

	if (freeze) {
		stopCPUMonitor();
	} else {
		startCPUMonitor();
	}
}

/**
 * Detiene el monitor de CPU.
 */
function stopCPUMonitor() {

	if (this.cpuMonitorInterval) {
		clearInterval(this.cpuMonitorInterval);
		this.cpuMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de CPU.
 */
function startCPUMonitor() {

	stopCPUMonitor();
	this.cpuMonitorInterval = setInterval(function() { 		

		// Historial de actividad de la CPU.
		if (CPUMonitor.cpuHistoryPlot) {
			CPUMonitor.cpuHistoryPlot.replot({ data: [[0], [0], [0]] });
		}

		// Carga instantanea de la CPU.
		if (CPUMonitor.cpuLoadInstantPlot) {
			CPUMonitor.cpuLoadInstantPlot.replot({ data: [] });
		}

	}, 1000);
}