<%@include file="includes/locations.jsp" %>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.AbstractList"%>
<%@page import="com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%!Long period;%>
<%!Long timeRange;%>
	
<%
	period = (Long)request.getAttribute("period");
	timeRange = (Long)request.getAttribute("timeRange");
%>
<link class="include" rel="stylesheet" type="text/css" href="<%=cssURL%>/jqplot/jquery.jqplot.min.css"></link>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/jquery.jqplot.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.cursor.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.barRenderer.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.dateAxisRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasAxisTickRenderer.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/custom/jqplot.bytesTickFormatter.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.enhancedLegendRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasTextRenderer.min.js"></script>

<script type="text/javascript">
	

	function printObject(o) {
		  var out = '';
		  for (var p in o) {
		    out += p + ': ' + o[p] + '\n';
		  }
		  return out;
		}
</script>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>Monitor de sistema</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la instancia<p/>
			<input id="memoryFreeze" type="checkbox" onClick="freezeMemoryMonitor();" checked> Freeze</input>
			<!-- Memoria -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Distribuci&oacute;n y consumo de memoria</div>
				<div class='panel-body'>
					<div class='row'>
						<div class='col-sm-8'>
							<div id='memoryHistory' style='width:700px; height:300px;'></div>
						</div>
						<div class='col-sm-2'>
							<div id='committedMemoryInstant' style='width:120px; height:300px;'></div>
						</div>
						<div class='col-sm-2'>
							<div id='usedMemoryInstant' style='width:120px; height:300px;'></div>
						</div>
					</div>
				</div>
			</div>
			<input id="cpuFreeze" type="checkbox" onClick="freezeCPUMonitor();" checked> Freeze</input>
			<!-- Procesador -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Uso del procesador</div>
				<div class='panel-body'>
					<div class='row'>
						<div class='col-sm-8'>
							<div id='cpuHistory' style='width:700px; height:300px;'></div>
						</div>
						<div class='col-sm-2'>
							<div id='cpuInstant' style='width:120px; height:300px;'></div>
						</div>
						<div class='col-sm-2'>
							<div id='usedMemoryInstant' style='width:120px; height:300px;'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	
	$.jqplot.config.enablePlugins = true;
	
	// Alimentador de información de la gráfica de distribución de memoria en el tiempo.
	var memoryHistoryDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Envío.
		xhr.send(JSON.stringify({period:${period}, timeRange:${timeRange}}));
		
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
	};
	
	// La gráfica de distribución de memoria en el tiempo.
	var memoryHistory = $.jqplot('memoryHistory', [], 
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
        series:[
                { color: '#ff1111', showMarker: false },
                { color: '#00b3ff', showMarker: false },
                { color: '#90d91d', showMarker: false },
                { color: '#fcc226', showMarker: false },
                { color: '#cffe2e', showMarker: false },
                { color: '#9ab66e', showMarker: false }
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
			url: '<%=servicesURL%>/system/memory'
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
	
	// Alimentador de información de la gráfica de distribución de memoria dipsonible instantanea.
	var committedMemoryInstantDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();

		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Envío.
		xhr.send(JSON.stringify({period:null, timeRange:null}));
		
		var jsonResponse = JSON.parse(xhr.responseText);
		
		var committedMemory = parseInt(jsonResponse["data"]["TotalCommitted"], 10); 
		var freeMemory = parseInt(jsonResponse["data"]["TotalMaxMemory"], 10)-committedMemory;
		
		ret = [[committedMemory], [freeMemory]];
		
	    return ret;
	};

	var committedMemoryInstant = jQuery.jqplot ('committedMemoryInstant', [], {
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
			url: '<%=servicesURL%>/system/memory'
			}
		});
	
	// Alimentador de información de la gráfica de distribución de memoria usada instantanea.
	var usedMemoryInstantDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();

		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Envío.
		xhr.send(JSON.stringify({period:null, timeRange:null}));
		
		var jsonResponse = JSON.parse(xhr.responseText);
		
		var committedMemory = parseInt(jsonResponse["data"]["TotalCommitted"], 10); 
		var usedHeapMemory = parseInt(jsonResponse["data"]["UsedHeapMemory"], 10); 
		var usedNonHeapMemory = parseInt(jsonResponse["data"]["UsedNonHeapMemory"], 10); 
		var freeMemory = committedMemory - usedHeapMemory - usedNonHeapMemory;
		
		ret = [[usedNonHeapMemory], [usedHeapMemory], [freeMemory]];
		
	    return ret;
	};

	var usedMemoryInstant = jQuery.jqplot ('usedMemoryInstant', [], {
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
	      show: false,
	    },
	    dataRenderer: usedMemoryInstantDataRenderer,
		dataRendererOptions: {
			url: '<%=servicesURL%>/system/memory'
			}
		});

	// Alimentador de información de la gráfica de actividad del procesador en el tiempo.
	var cpuHistoryDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Envío.
		xhr.send(JSON.stringify({period:${period}, timeRange:${timeRange}}));
		
		var jsonResponse = JSON.parse(xhr.responseText);
		
		ret = [
		       /*
		       jsonResponse["data"]["PeakThreadCount"],
		       jsonResponse["data"]["DaemonThreadCount"],
		       jsonResponse["data"]["NumberOfThreads"],
*/
		       jsonResponse["data"]["SystemCPULoad"],
		       jsonResponse["data"]["ApplicationCPULoad"],
		       jsonResponse["data"]["CPULoad"]
		       ];
	
	    return ret;
	};
	
	// La gráfica de actividad del procesador en el tiempo.
	var cpuHistory = $.jqplot('cpuHistory', [], 
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
        series:[
                /*
                { color: '#ff1111', showMarker: false },
                { color: '#00b3ff', showMarker: false },
                { color: '#90d91d', showMarker: false },
                */
                { color: '#fcc226', showMarker: false },
                { color: '#cffe2e', showMarker: false },
                { color: '#9ab66e', showMarker: false }
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
					/*
					formatter: $.jqplot.BytesTickFormatter,
					formatString:'%d',
					*/
					fontSize: '10pt'
		        },
				min: 0
			}
        },
		dataRenderer: cpuHistoryDataRenderer,
		dataRendererOptions: {
			url: '<%=servicesURL%>/system/cpu'
		},
        legend: {
			renderer: jQuery.jqplot.EnhancedLegendRenderer,
            labels: [
                     /*
                     'Pico m&aacute;ximo',
                     'Demonios', 
                     'Hilos',
                     */
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
	
	function freezeMemoryMonitor() {
        if (document.getElementById('memoryFreeze').checked) {
        	stopMemoryMonitor();
        } else {
        	startMemoryMonitor();
        }
    }

	function stopMemoryMonitor() {
		if (this.memoryMonitorInterval) {
			clearInterval(this.memoryMonitorInterval);
			this.memoryMonitorInterval = null;
		}
	}

	function startMemoryMonitor() {
		stopMemoryMonitor();
		this.memoryMonitorInterval = setInterval(function() { 		
			// Historial de memoria.
			memoryHistory.replot({ data: [] });
	
			// Instante de memoria disponible.
			committedMemoryInstant.replot({ data: [] });
			
			// Instante de memoria usada.
			usedMemoryInstant.replot({ data: [] }); 
		}, 1000);
	}
	
	function freezeCPUMonitor() {
        if (document.getElementById('cpuFreeze').checked) {
        	stopCPUMonitor();
        } else {
        	startCPUMonitor();
        }
    }
	
	function stopCPUMonitor() {
		if (this.cpuMonitorInterval) {
			clearInterval(this.cpuMonitorInterval);
			this.cpuMonitorInterval = null;
		}
	}

	function startCPUMonitor() {
		stopCPUMonitor();
		this.cpuMonitorInterval = setInterval(function() { 		
			// Historial de actividad de la CPU.
			cpuHistory.replot({ data: [] });
		}, 1000);
	}
	
	stopMemoryMonitor();
	stopCPUMonitor();
</script>