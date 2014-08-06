<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jqplot/jquery.jqplot.min.css"/>"/>

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
							<input id="memoryHistoryFreeze" type="checkbox" onClick="freezeMemoryHistoryMonitor(this.checked);" checked/>
							<label for="memoryHistoryFreeze">Congelar</label>
							<div id='memoryHistory' style='width:100%; height:300px;'></div>
						</div>
						<div class='col-sx-6 col-md-4'>
							<input id="memoryFreeze" type="checkbox" onClick="freezeMemoryInstantMonitor(this.checked);" checked/>
							<label for="memoryFreeze">Congelar</label>
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
							<input id="cpuHistoryFreeze" type="checkbox" onClick="freezeCPUHistoryMonitor(this.checked);" checked/>
							<label for="cpuHistoryFreeze">Congelar</label>
							<div id='cpuHistory' style='width:100%; height:300px;'></div>
						</div>
						<div class='col-sm-3'>
							<input id="cpuFreeze" type="checkbox" onClick="freezeCPUInstantMonitor(this.checked);" checked/>
							<label for="cpuFreeze">Congelar</label>
							<div id='cpuInstant' style='width:100%; height:300px;'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

	initSystemMonitor(1, 300, 
			'memoryHistory', 'committedMemoryInstant', 'usedMemoryInstant',
			'cpuHistory', 'cpuInstant');
</script>

