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
<script class="include" type="text/javascript" src="<%=jsURL%>/system_monitor.js"></script>

<!-- Panel de monitorización del sistema. -->
<div id="systemMonitor">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>Monitor de sistema</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la instancia<p/>
			<!-- Memoria -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Distribuci&oacute;n y consumo de memoria</div>
				<div class='panel-body'>
					<div class='row-fluid'>
						<div class='col-sx-6 col-md-8'>
							<input id="memoryHistoryFreeze" type="checkbox" onClick="freezeMemoryHistoryMonitor(this.checked);" checked> Congelar</input>
							<div id='memoryHistory' style='width:100%; height:300px;'></div>
						</div>
						<div class='col-sx-6 col-md-4'>
							<input id="memoryFreeze" type="checkbox" onClick="freezeMemoryInstantMonitor(this.checked);" checked> Congelar</input>
							<div class='container-fluid'>
								<div class='col-sx-6 col-md-6'>
									<div id='committedMemoryInstant' style='width:100%; height:300px%;'></div>
								</div>
								<div class='col-sx-6 col-md-6'>
									<div id='usedMemoryInstant' style='width:100%; height:300px;'></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Procesador -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Uso del procesador</div>
				<div class='panel-body'>
					<div class='row-fluid'>
						<div class='col-sm-9'>
							<input id="cpuHistoryFreeze" type="checkbox" onClick="freezeCPUHistoryMonitor(this.checked);" checked> Congelar</input>
							<div id='cpuHistory' style='width:100%; height:300px;'></div>
						</div>
						<div class='col-sm-3'>
							<input id="cpuFreeze" type="checkbox" onClick="freezeCPUInstantMonitor(this.checked);" checked> Congelar</input>
							<div id='cpuInstant' style='width:100%; height:300px;'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

	initSystemMonitor(${period}, ${timeRange}, 
			'memoryHistory', 'committedMemoryInstant', 'usedMemoryInstant',
			'cpuHistory', 'cpuInstant');

</script>