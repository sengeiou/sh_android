<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!-- Panel de monitorización de una caché de entidad del sistema. -->
<div id="systemCacheMonitor">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick='showCacheMonitor("${cacheManager}", "${cache}");' type='button' class='btn btn-default btn-sm pull-right'>
   			<span class='glyphicon glyphicon-th-list'></span>
   		</button>
		<div class="panel-heading"><h3>Monitor de la cach&eacute; ${cache}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la cach&eacute;<p/>
			<!-- Heap -->
			<div class='panel panel-info'>
				<div class='panel-heading'>Accesos a memoria</div>
				<div class='panel-body'>
					<div class='row-fluid'>
						<form class="form-horizontal">
							<div class="form-group">
					            <label class="control-label col-sm-1">Periodo</label>
								<div class='col-md-2'>
									<select class="form-control" name="period" required="required">
										<option value="1s">1 Segundo</option>
										<option value="5s">5 Segundos</option>
										<option value="1m">1 Minuto</option>
										<option value="5m">5 Minutos</option>
										<option value="15m">15 Minutos</option>
										<option value="1h">1 Hora</option>
										<option value="6h">6 Horas</option>
										<option value="1d">1 Día</option>
									</select>
					            </div>							
								<label class="control-label col-sm-1">desde</label>
								<div class='col-sm-2 col-md-2'>
						            <div class="form-group">
						                <div class='input-group date' id='datetimePickerHeapAccessFrom'>
						                    <input type='text' class="form-control" />
						                    <span class="input-group-addon">
						                    	<span class="glyphicon glyphicon-calendar"></span>
						                    </span>
						                </div>
						            </div>
					            </div>
					            <label class="control-label col-sm-1">hasta</label>
					            <div class='col-sm-3 col-md-3'>
						            <div class="form-group">
						                <div class='input-group date' id='datetimePickerHeapAccessTo'>
						                    <input type='text' class="form-control" />
						                    <span class="input-group-addon datepickerbutton">
						                    	<span class="glyphicon glyphicon-calendar"></span>
						                    </span>
						                    <span id="removeDatetimePickerHeapAccessTo" class="input-group-addon">
						            			<span class="glyphicon glyphicon-remove"></span>
						                    </span>
						                </div>
						            </div>
					            </div>
							</div>
						</form>
					</div>
			        <script type="text/javascript">
			            $(function () {
			                $('#datetimePickerHeapAccessFrom').datetimepicker({
			                	language: "es",
			                	pick12HourFormat: false,
			                	icons: {
									time: "glyphicon glyphicon-time",
									date: "glyphicon glyphicon-calendar",
									up: "glyphicon glyphicon-chevron-up",
									down: "glyphicon glyphicon-chevron-down"
			                    }
			                });
			                
			                $('#datetimePickerHeapAccessTo').datetimepicker({
			                	language: "es",
			                	pick12HourFormat: false,
			                	icons: {
									time: "glyphicon glyphicon-time",
									date: "glyphicon glyphicon-calendar",
									up: "glyphicon glyphicon-chevron-up",
									down: "glyphicon glyphicon-chevron-down"
			                    }
			                });
			                
							$("#datetimePickerHeapAccessFrom").on("dp.change", function (e) {
							   $('#datetimePickerHeapAccessTo').data("DateTimePicker").setMinDate(e.date);
							});
							$("#datetimePickerHeapAccessTo").on("dp.change", function (e) {
							   $('#datetimePickerHeapAccessFrom').data("DateTimePicker").setMaxDate(e.date);
							});
							$("#removeDatetimePickerHeapAccessTo").on("click", function (e) {
								   $('#datetimePickerHeapAccessTo').data("DateTimePicker").setDate(null);
							});
			            });
			        </script>
					<div class='col-sx-6 col-md-8'>
						<div id='memoryHistory' style='width:100%; height:300px;'></div>
					</div>
					<div class='col-sx-6 col-md-4'>
						<div class='col-sx-6 col-md-6'>
							<div id='committedMemoryInstant' style='width:100%; height:300px%;'></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
/*
	initSystemCacheMonitor(${period}, ${offset}, ${timeRange},
			'cacheHeapAccessHistory', 'cacheHeapAccessInstant',
			'cacheHeapUseHistory', 'cacheHeapUseInstant',
			'cacheDiskAccessHistory', 'cacheDiskAccessInstant',
			'cacheDiskUseHistory', 'cacheDiskUseInstant');
			*/
</script>