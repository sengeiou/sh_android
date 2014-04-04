<%@include file="includes/locations.jsp"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.AbstractList"%>
<%@page import="com.fav24.dataservices.monitoring.MonitorSample"%>
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

<script class="include" type="text/javascript" src="<%=jsURL%>/system_monitor/memory_monitor.js"></script>
<script class="include" type="text/javascript" src="<%=jsURL%>/system_monitor/cpu_monitor.js"></script>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>Monitor de sistema</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la instancia<p/>
			<input id="memoryFreeze" type="checkbox" onClick="freezeMemoryMonitor(this.checked);" checked> Congelar</input>
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
			<input id="cpuFreeze" type="checkbox" onClick="freezeCPUMonitor(this.checked);" checked> Congelar</input>
			<!-- Procesador -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Uso del procesador</div>
				<div class='panel-body'>
					<div class='row'>
						<div class='col-sm-9'>
							<div id='cpuHistory' style='width:700px; height:300px;'></div>
						</div>
						<div class='col-sm-3'>
							<div id='cpuInstant' style='width:150px; height:300px;'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

	initMemoryMonitor(${period}, ${timeRange}, 'memoryHistory', 'committedMemoryInstant', 'usedMemoryInstant');
	initCPUMonitor(${period}, ${timeRange}, 'cpuHistory', 'cpuInstant');

</script>