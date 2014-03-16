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
		output.append("<div id='memoryHistory' style='width:700px; height:250px;'></div>");
		output.append("</div>");

		output.append("<div class='col-sm-4'>");
		output.append("<div id='memoryInstant' style='width:250px; height:250px;'></div>");
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
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasTextRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.canvasAxisLabelRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.pieRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jqplot/plugins/jqplot.enhancedLegendRenderer.min.js"></script>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>Informaci&oacute;n de los &uacute;ltimos ${timeRange} segundos</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<%=bodyContent%>
		</div>
	</div>
</div>

<script type="text/javascript">

	var memoryHistoryDataRenderer = function(url, plot, options)
	{
		var ret = null;
		
		var xhr = new XMLHttpRequest();
		
		xhr.open("POST", "<%=servicesURL%>/system/memory", false);
		xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
		// Envío.
		xhr.send(JSON.stringify({period:${period}, timeRange:${timeRange}}));
		
		var jsonResponse = JSON.parse(xhr.responseText);
		
		ret = [
		       jsonResponse["data"]["TotalInitMemory"],
		       jsonResponse["data"]["TotalMaxMemory"],
		       jsonResponse["data"]["TotalCommitted"],
		       jsonResponse["data"]["TotalUsedMemory"],
		       jsonResponse["data"]["UsedHeapMemory"],
		       jsonResponse["data"]["UsedNonHeapMemory"]
		       ];

	    return ret;
	};

	// The url for our json data
	var memoryHistoryURL = '<%=servicesURL%>/system/memory';
	
	var memoryHistory = $.jqplot('memoryHistory', memoryHistoryURL, 
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
            show: true,
            zoom: true,
            looseZoom: true,
            showTooltip: true
        },
        series:[
                {
                	showMarker:false
                	},
                {
                	showMarker:false
                	},
                {
                	showMarker:false
                	},
                {
                	showMarker:false
                	},
                {
                	showMarker:false
                	},
                {
                	showMarker:false
                	}
                ],
        axes:{
			xaxis:{
/*
				renderer:$.jqplot.DateAxisRenderer,
				tickOptions:{formatString:'%d/%m/%Y %H:%M:%S'},
				tickInterval:'1 minutes',
				*/
				tickInterval:60000,
				label:'Tiempo',
				labelRenderer: $.jqplot.CanvasAxisLabelRenderer
				},
			yaxis:{
				min: 0,
				//showTicks: false,
				//showTickMarks: false,
				label:'Espacio (bytes)',
				labelRenderer: $.jqplot.CanvasAxisLabelRenderer
			}
        },
		dataRenderer: memoryHistoryDataRenderer,
		dataRendererOptions: {
			unusedOptionalUrl: memoryHistoryURL
		},
        legend: {
			renderer: jQuery.jqplot.EnhancedLegendRenderer,
            labels: ['TotalInitMemory', 
                     'TotalMaxMemory',
                     'TotalCommitted',
                     'TotalUsedMemory',
                     'UsedHeapMemory',
                     'UsedNonHeapMemory'
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

	/*
	var memoryInstant = jQuery.jqplot ('memoryInstant', [], 
	    { 
		grid: {
			drawBorder: false,
			drawGridlines: false,
			background: '#ffffff',
			shadow: false
		},
		axesDefaults: {
             
        },
        animate: true,
        // Will animate plot on calls to plot1.replot({resetAxes:true})
        animateReplot: true,
        cursor: {
            show: true,
            zoom: true,
            looseZoom: true,
            showTooltip: false
        },
        seriesDefaults: {
				trendline: {
					show: true
					},
				shadow: false,
				// Make this a pie chart.
				renderer: jQuery.jqplot.PieRenderer,
				rendererOptions: {
					// Put data labels on the pie slices.
					// Turn off filling of slices.
					fill: true,
					showDataLabels: true,
					// Add a margin to seperate the slices.
					sliceMargin: 4,
					// stroke the slices with a little thicker line.
					lineWidth: 5
				}
			},
		legend: {
				renderer: jQuery.jqplot.EnhancedLegendRenderer,
	            labels: ['heap', 'non-heap'],
				show: true,
				showLabels: true,
				showSwatches: false,
				rowSpacing: '10px',
				marginLeft: '-150px',
				placement: 'outsideGrid',
				location: 'e',
				border: 'none'
			}
		});
	*/

	function chartReplots()
	{
		var series = memoryHistory.memoryHistoryDataRenderer(memoryHistoryURL, null, null);
		var options = { data: series };


		memoryHistory.replot( options );
	}
	
	var dummyVar = setInterval(function() { chartReplots(); }, 1000);
</script>