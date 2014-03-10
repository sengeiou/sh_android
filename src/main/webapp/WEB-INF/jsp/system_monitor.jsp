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
		
		AbstractList<MonitorSample> memoryStatus = (AbstractList<MonitorSample>)request.getAttribute("MemoryStatus");
		AbstractList<MonitorSample> cpuActivity = (AbstractList<MonitorSample>)request.getAttribute("CPUActivity");
		AbstractList<MonitorSample> storageStatus = (AbstractList<MonitorSample>)request.getAttribute("StorageStatus");

		output.append("<p>Informaci&oacute;n y estado de la instancia<p/>");

		// Memoria.
		output.append("<div class='panel panel-info'>");
		output.append("<div class='panel-heading'>Distribuci&oacute;n y consumo de memoria</div>");
		output.append("<div class='panel-body'>");

		output.append("<div class='row'>");
		
		output.append("<div class='col-sm-6'>");
		output.append("<div id='memoryHistory' style='width:500px; height:250px;'></div>");
		output.append("</div>");

		output.append("<div class='col-sm-6'>");
		output.append("<div id='memoryInstant' style='width:250px; height:250px;'></div>");
		output.append("</div>");
		
		output.append("</div>");

		output.append("</div>");
		output.append("</div>");
		
		long heapPercent = (entityCache.getMaxBytesLocalHeap() * 100) / entityCacheManager.getMaxBytesLocalHeap();
		output.append("<script type='text/javascript'>");
		output.append('\n');
		output.append("var memoryHistoryData = [");
		output.append(heapPercent);
		output.append(",").append(100-heapPercent);
		output.append("];");

		long diskPercent = (entityCache.getMaxBytesLocalDisk() * 100) / entityCacheManager.getMaxBytesLocalDisk();
		output.append('\n');
		output.append("var memoryInstantData = [");
		output.append(diskPercent);
		output.append(",").append(100-diskPercent);
		output.append("];");

		output.append("</script>");
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<link class="include" rel="stylesheet" type="text/css" href="<%=cssURL%>/jplot/jquery.jqplot.min.css"></link>
<script class="include" type="text/javascript" src="<%=jsURL%>/jplot/jquery.jqplot.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jplot/plugins/jqplot.pieRenderer.min.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/jplot/plugins/jqplot.enhancedLegendRenderer.min.js"></script>

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

	var memoryHistory = jQuery.jqplot ('memoryHistory', [memoryHistoryData], 
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
	            labels: ['<%=entity%>', '<%=cacheManagerName%>'],
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

	
	var memoryInstant = jQuery.jqplot ('memoryInstant', [memoryInstantData], 
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
	            labels: ['<%=entity%>', '<%=cacheManagerName%>'],
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
</script>