<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!-- Panel de monitorización de una caché de entidad del sistema. -->
<div id="systemCacheMonitor">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick="showCacheMonitor('${cacheManager}', '${cache}');" type="button" class="btn btn-default btn-sm pull-right">
   			<span class="glyphicon glyphicon-th-list"></span>
   		</button>
		<div class="panel-heading"><h3>Monitor de la cach&eacute; ${cache}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<p>Informaci&oacute;n y estado de la cach&eacute;<p/>
			<!-- Heap -->
			<div class="panel panel-info">
				<div class="panel-heading">Accesos a memoria</div>
				<div class="panel-body">
					<div class="row-fluid">
						<form class="form-horizontal">
							<div class="form-group">
								<div class="col-sm-1 col-md-1 text-right">
					            	<label class="control-label">Periodo</label>
					            </div>
								<div class="col-sm-2 col-md-2">
									<select class="form-control" id="period" required="required">
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
								<div class="col-sm-1 col-md-1 text-right">
					            	<label class="control-label">desde</label>
					            </div>
								<div class="col-md-3">
					                <div class="input-group date" id="datetimePickerHeapAccessFrom">
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
					                <div class="input-group date" id="datetimePickerHeapAccessTo">
					                    <input type="text" class="form-control" />
					                    <span class="input-group-addon datepickerbutton">
					                    	<span class="glyphicon glyphicon-calendar"></span>
					                    </span>
					                    <span id="removeDatetimePickerHeapAccessTo" class="input-group-addon">
					            			<span class="glyphicon glyphicon-remove"></span>
					                    </span>
					                </div>
					            </div>
							</div>
						</form>
					</div>
					<div class="row-fluid">
						<div class="col-sx-6 col-md-8">
							<div class="form-control-group">
								<div id="cacheHeapHistoryContainer">
									<div class="canvas-container">
										<canvas style="width: 100%; height: 110px;" class="form-control" id="cacheHeapHistory" width="" height="110"></canvas>
										
										<div class="chart-legend">
											<ul class="list-inline line-legend">
												<li>
													<span style="margin-right:5px; background-color:rgba(220,220,220,1)"></span>Hits
												</li>
												<li>
													<span style="margin-right:5px; background-color:rgba(151,187,205,1)"></span>Fails
												</li>
											</ul>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="col-sx-6 col-md-4">
							<div class="form-control-group">
								<div id="cacheHeapInstantContainer" >
									<canvas style="width: 100%; height: 250px;" class="form-control" id="cacheHeapInstant" width="" height="250"></canvas>
								</div>
							</div>
						</div>
					</div>
			        <script type="text/javascript">
			            $(function () {
			                $("#datetimePickerHeapAccessFrom").datetimepicker({
			                	language: "es",
			                	pick12HourFormat: false,
			                	icons: {
									time: "glyphicon glyphicon-time",
									date: "glyphicon glyphicon-calendar",
									up: "glyphicon glyphicon-chevron-up",
									down: "glyphicon glyphicon-chevron-down"
			                    }
			                });
			                
			                $("#datetimePickerHeapAccessTo").datetimepicker({
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
							   $("#datetimePickerHeapAccessTo").data("DateTimePicker").setMinDate(e.date);
							});
							$("#datetimePickerHeapAccessTo").on("dp.change", function (e) {
							   $("#datetimePickerHeapAccessFrom").data("DateTimePicker").setMaxDate(e.date);
							});
							$("#removeDatetimePickerHeapAccessTo").on("click", function (e) {
								   $("#datetimePickerHeapAccessTo").data("DateTimePicker").setDate(null);
							});
			            });

			            // Activa el responsive de las gráficas. --> El redimensionamiento del canvas.
						$(function() {
							var e = Chart.helpers;

							Chart.defaults.global.responsive = true;
							Chart.defaults.global.animation = true;
			            });
			        </script>
				</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">

// Definición de los contextos gráficos.
var memoryHistoryCanvas = document.getElementById("cacheHeapHistory");
var cacheHeapInstantCanvas = document.getElementById("cacheHeapInstant");
var memoryHistoryContext = memoryHistoryCanvas.getContext("2d");
var cacheHeapInstantContext = cacheHeapInstantCanvas.getContext("2d");

var data = {
	    labels: ["January", "February", "March", "April", "May", "June", "July"],
	    datasets: [
	        {
	            label: "Hits",
	            fillColor: "rgba(220,220,220,0.2)",
	            strokeColor: "rgba(220,220,220,1)",
	            pointColor: "rgba(220,220,220,1)",
	            pointStrokeColor: "#fff",
	            pointHighlightFill: "#fff",
	            pointHighlightStroke: "rgba(220,220,220,1)",
	            data: [65, 59, 80, 81, 56, 55, 40]
	        },
	        {
	            label: "Fails",
	            fillColor: "rgba(151,187,205,0.2)",
	            strokeColor: "rgba(151,187,205,1)",
	            pointColor: "rgba(151,187,205,1)",
	            pointStrokeColor: "#fff",
	            pointHighlightFill: "#fff",
	            pointHighlightStroke: "rgba(151,187,205,1)",
	            data: [28, 48, 40, 19, 86, 27, 90]
	        }
	    ]
	};
	
var options = {

	    ///Boolean - Whether grid lines are shown across the chart
	    scaleShowGridLines : true,

	    //String - Colour of the grid lines
	    scaleGridLineColor : "rgba(0,0,0,.05)",

	    //Number - Width of the grid lines
	    scaleGridLineWidth : 1,

	    //Boolean - Whether the line is curved between points
	    bezierCurve : true,

	    //Number - Tension of the bezier curve between points
	    bezierCurveTension : 0.4,

	    //Boolean - Whether to show a dot for each point
	    pointDot : true,

	    //Number - Radius of each point dot in pixels
	    pointDotRadius : 4,

	    //Number - Pixel width of point dot stroke
	    pointDotStrokeWidth : 1,

	    //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
	    pointHitDetectionRadius : 20,

	    //Boolean - Whether to show a stroke for datasets
	    datasetStroke : true,

	    //Number - Pixel width of dataset stroke
	    datasetStrokeWidth : 2,

	    //Boolean - Whether to fill the dataset with a colour
	    datasetFill : true
	};

var cacheHeapHistory = new Chart(memoryHistoryContext).Line(data, options);
//$("#cacheHeapHistoryContainer").append(cacheHeapHistory.generateLegend());


var data = [
            {
                value: 300,
                color:"#F7464A",
                highlight: "#FF5A5E",
                label: "Red"
            },
            {
                value: 50,
                color: "#46BFBD",
                highlight: "#5AD3D1",
                label: "Green"
            },
            {
                value: 100,
                color: "#FDB45C",
                highlight: "#FFC870",
                label: "Yellow"
            }
        ];
        
        
var options = {
		
    //Boolean - Whether we should show a stroke on each segment
    segmentShowStroke : true,

    //String - The colour of each segment stroke
    segmentStrokeColor : "#fff",

    //Number - The width of each segment stroke
    segmentStrokeWidth : 2,

    //Number - The percentage of the chart that we cut out of the middle
    percentageInnerCutout : 50, // This is 0 for Pie charts

    //Number - Amount of animation steps
    animationSteps : 100,

    //String - Animation easing effect
    animationEasing : "easeOutBounce",

    //Boolean - Whether we animate the rotation of the Doughnut
    animateRotate : true,

    //Boolean - Whether we animate scaling the Doughnut from the centre
    animateScale : true
};

var cacheHeapInstant = new Chart(cacheHeapInstantContext).Pie(data, options);
$("#cacheHeapInstantContainer").append(cacheHeapInstant.generateLegend());

/*
	initSystemCacheMonitor(${period}, ${offset}, ${timeRange},
			"cacheHeapAccessHistory", "cacheHeapAccessInstant",
			"cacheHeapUseHistory", "cacheHeapUseInstant",
			"cacheDiskAccessHistory", "cacheDiskAccessInstant",
			"cacheDiskUseHistory", "cacheDiskUseInstant");
			*/
</script>