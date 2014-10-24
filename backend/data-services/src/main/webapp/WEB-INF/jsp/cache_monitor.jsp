<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!-- Panel de monitorización de una caché de entidad del sistema. -->
<div id="systemCacheMonitor">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick="sendGetRequest('cache/cacheManagerConfiguration?cacheManager=${cacheManager}');" type="button" class="btn btn-default btn-sm pull-right">
   			<span class="glyphicon glyphicon-th-list"></span>
   		</button>
		<div class="panel-heading"><h3>Monitor de la cach&eacute; ${cache}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la cach&eacute;<p/>
			<div class="panel-body">
				<form class="form-horizontal">
					<div class="form-group">
						<div class="col-sm-1 col-md-1 text-right">
			            	<label class="control-label">Periodo</label>
			            </div>
						<div class="col-sm-3 col-md-3">
							<select class="form-control" id="period" required="required">
								<option value="1">1 Segundo</option>
								<option value="5">5 Segundos</option>
								<option selected="selected" value="60">1 Minuto</option>
								<option value="300">5 Minutos</option>
								<option value="900">15 Minutos</option>
								<option value="3600">1 Hora</option>
								<option value="21600">6 Horas</option>
								<option value="86400">1 Día</option>
							</select>
						</div>
						<div class="col-sm-1 col-md-1 text-right">
			            	<label class="control-label">durante</label>
			            </div>
						<div class="col-sm-3 col-md-3">
							<select class="form-control" id="segment" required="required">
								<option value="30">&Uacute;ltimos 30 segundos</option>
								<option value="60">&Uacute;ltimo minuto</option>
								<option value="300">&Uacute;ltimos 5 minutos</option>
								<option selected="selected" value="3600">&Uacute;ltima hora</option>
								<option value="10800">&Uacute;ltimas 3 horas</option>
								<option value="21600">&Uacute;ltimas 6 horas</option>
								<option value="43200">&Uacute;ltimas 12 horas</option>
								<option value="86400">&Uacute;ltimas 24 horas</option>
								<option value="259200">&Uacute;ltimas 3 d&iacute;as</option>
								<option value="604800">&Uacute;ltima semana</option>
								<option value="1209600">&Uacute;ltimas 2 semanas</option>
								<option value="-1">Entre fechas</option>
							</select>
						</div>
			            <div class="col-sm-3 col-md-3">
			            	<button id="applyTimeWindowButton" type="button" class="form-control btn btn-default">
					   			<span class="glyphicon glyphicon-refresh"></span>
					   			Aplicar
					   		</button>
				   		</div>
					</div>
					<!-- Intervalo de fechas. -->
					<div class="form-group">
						<div class="col-sm-1 col-md-1 text-right">
			            	<label class="control-label">desde</label>
			            </div>
						<div class="col-md-3">
			                <div class="input-group date" id="datetimePickerAccessFrom">
			                    <input type="text" class="form-control" />
			                    <span class="input-group-addon">
			                    	<span class="glyphicon glyphicon-calendar"></span>
			                    </span>
			                </div>
			            </div>
			            <div class="col-sm-1 col-md-1 text-right">
			            	<label class="control-label">hasta</label>
			            </div>
			            <div class="col-md-3">
			                <div class="input-group date" id="datetimePickerAccessTo">
			                    <input type="text" class="form-control" />
			                    <span class="input-group-addon datepickerbutton">
			                    	<span class="glyphicon glyphicon-calendar"></span>
			                    </span>
			                    <span class="remove input-group-addon">
			            			<span class="glyphicon glyphicon-remove"></span>
			                    </span>
			                </div>
			            </div>
					</div>
				</form>
			</div>
			<!-- Heap Hits and Fails -->
			<div class="panel panel-info">
				<div class="panel-heading">Accesos a memoria</div>
				<div class="panel-body">
					<div class="row-fluid">
						<div class="col-sx-6 col-md-8">
							<div class="form-control-group">
								<div id="cacheHAHeapHistory">
									<canvas style="width: 100%; height: 200px;" class="form-control" width="" height="200px"></canvas>
									<div class="chart-legend"></div>
								</div>
							</div>
						</div>
						<div class="col-sx-6 col-md-4">
							<div class="form-control-group">
								<div id="cacheHAHeap" >
									<canvas style="width: 100%; height: 200px;" class="form-control" width="" height="200px"></canvas>
                                    <div class="chart-legend"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Disk Hits and Fails -->
			<div class="panel panel-info">
				<div class="panel-heading">Accesos a disco</div>
				<div class="panel-body">
					<div class="row-fluid">
						<div class="col-sx-6 col-md-8">
							<div class="form-control-group">
								<div id="cacheHADiskHistory">
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
									<div class="chart-legend"></div>
								</div>
							</div>
						</div>
						<div class="col-sx-6 col-md-4">
							<div class="form-control-group">
								<div id="cacheHADisk" >
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
                                    <div class="chart-legend"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Heap amount -->
			<div class="panel panel-info">
				<div class="panel-heading">Uso de memoria</div>
				<div class="panel-body">
					<div class="row-fluid">
						<div class="col-sx-6 col-md-8">
							<div class="form-control-group">
								<div id="cacheHeapHistory">
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
									<div class="chart-legend"></div>
								</div>
							</div>
						</div>
						<div class="col-sx-6 col-md-4">
							<div class="form-control-group">
								<div id="cacheHeap" >
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
                                    <div class="chart-legend"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<!-- Disk amount -->
			<div class="panel panel-info">
				<div class="panel-heading">Uso de disco</div>
				<div class="panel-body">
					<div class="row-fluid">
						<div class="col-sx-6 col-md-8">
							<div class="form-control-group">
								<div id="cacheDiskHistory">
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
									<div class="chart-legend"></div>
								</div>
							</div>
						</div>
						<div class="col-sx-6 col-md-4">
							<div class="form-control-group">
								<div id="cacheDisk" >
									<canvas style="width: 100%; height: 200px;" class="form-control" id="" width="" height="200px"></canvas>
                                    <div class="chart-legend"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
initCacheMonitor('${cacheManager}', '${cache}', ${maxBytesLocalHeap}, ${maxBytesLocalDisk},
		'period', 'segment', 'datetimePickerAccessFrom', 'datetimePickerAccessTo', 'applyTimeWindowButton', 
		'cacheHAHeapHistory', 'cacheHAHeap', 
		'cacheHADiskHistory', 'cacheHADisk', 
		'cacheHeapHistory', 'cacheHeap', 
		'cacheDiskHistory', 'cacheDisk'
);
</script>