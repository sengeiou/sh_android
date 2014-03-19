<%@include file="includes/locations.jsp" %>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.AbstractList"%>
<%@page import="com.fav24.dataservices.monitoring.SystemMonitoring.MonitorSample"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%!String bodyContent;%>
<%!Long period;%>
<%!Long timeRange;%>
	
<%
	StringBuilder output = new StringBuilder();
	period = (Long)request.getAttribute("period");
	timeRange = (Long)request.getAttribute("timeRange");
	
	try {
		
		output.append("<p>Informaci&oacute;n y estado de la instancia<p/>");

		// Memoria.
		output.append("<div class='panel panel-info'>");
		output.append("<div class='panel-heading'>Distribuci&oacute;n y consumo de memoria</div>");
		output.append("<div class='panel-body'>");

		output.append("<div class='row'>");
		
		output.append("<div class='col-sm-8'>");
		output.append("<div id='memoryHistory' style='width:700px; height:300px;'></div>");
		output.append("</div>");

		output.append("<div class='col-sm-2'>");
		output.append("<div id='committedMemoryInstant' style='width:120px; height:300px;'></div>");
		output.append("</div>");

		output.append("<div class='col-sm-2'>");
		output.append("<div id='usedMemoryInstant' style='width:120px; height:300px;'></div>");
		output.append("</div>");
		
		output.append("</div>");

		output.append("</div>");
		output.append("</div>");

		output.append("</script>");
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<link class="include" rel="stylesheet" type="text/css" href="<%=cssURL%>/jqplot/jquery.jqplot.min.css"></link>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/jquery.jqplot.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.barRenderer.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.dateAxisRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.categoryAxisRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasAxisTickRenderer.min.js"></script>

<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/custom/jqplot.bytesTickFormatter.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.enhancedLegendRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasTextRenderer.min.js"></script>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>Monitor de sistema</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<%=bodyContent%>
		</div>
	</div>
</div>

<script type="text/javascript">
	
	$.jqplot.config.enablePlugins = true;

	function printObject(o) {
		  var out = '';
		  for (var p in o) {
		    out += p + ': ' + o[p] + '\n';
		  }
		  return out;
		}
	
	// Alimentador de informaci�n de la gr�fica de distribuci�n de memoria en el tiempo.
	var memoryHistoryDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Env�o.
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
	
	// La gr�fica de distribuci�n de memoria en el tiempo.
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
            looseZoom: false,
            showTooltip: false
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
	
	// Alimentador de informaci�n de la gr�fica de distribuci�n de memoria dipsonible instantanea.
	var committedMemoryInstantDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();

		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Env�o.
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
	
	// Alimentador de informaci�n de la gr�fica de distribuci�n de memoria usada instantanea.
	var usedMemoryInstantDataRenderer = function(url, plot, options)
	{
		var ret = null;
		var xhr = new XMLHttpRequest();

		xhr.open("POST", options["url"], false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Env�o.
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

	// Refresco de la informaci�n de las gr�ficas.
	function chartReplots()
	{
		// Historial de memoria.
		memoryHistory.replot({ data: [] });

		// Instante de memoria disponible.
		committedMemoryInstant.replot({ data: [] });
		
		// Instante de memoria usada.
		usedMemoryInstant.replot({ data: [] });
	}

	var dummyVar = setInterval(function() { chartReplots(); }, 1000);
</script>