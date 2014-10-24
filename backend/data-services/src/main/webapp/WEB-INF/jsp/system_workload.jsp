<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/general.css"/>"/>

<!-- Panel de información del trabajo realizado y en curso. -->
<div id="workload" class="container-fluid ">
	<div class="page-header">
		<p class="lead">Diagrama de carga de trabajo del sistema</p>
	</div>
	<div class="row">
		<div class="col-md-2 col-md-offset-0">
			<p class="text-left"><strong>Información desde el: <span class="measure" id="MeasureStartTime"></span></strong></p>
		</div>
		<div class="col-md-2 col-md-offset-0">
    		<button onClick='newWorkloadMeasurePeriod();' type='button' class='btn btn-default btn-sm pull-right'>
    			<span class='glyphicon glyphicon-time'></span>
    			Nuevo periodo de medidas
    		</button>
		</div>
		<div class="col-md-2 col-md-offset-2">
			<img style="height:120px;" src="<c:url value="/resources/img/workload/cloud.png"/>" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-4 col-md-offset-0">
			<p class="text-left"><strong>Peticiones entrantes</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <span class="measure" id="RequestsRate">- req/s</span><br/>
				<strong>Tasa m&aacute;xima:</strong> <span class="measure" id="RequestsRatePeak">- req/s</span><br/>
				<strong>Total:</strong> <span class="measure" id="TotalRequests">- req</span><br/>
				<strong>Errores:</strong> <span class="measure" id="TotalRequestsKo">- req</span><br/>
			</p>
		</div>
	</div>

	<div class="row">
		<div class="col-md-2 col-md-offset-5">
			<img style="height:80px;" src="<c:url value="/resources/img/workload/blue-flow.png"/>" class="img-responsive" alt="Internet">
		</div>
	</div>

	<div class="row">
		<div class="col-md-2 col-md-offset-4">
			<img style="height:120px;" src="<c:url value="/resources/img/workload/data-server.png"/>" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-6 col-md-offset-0">
			<p class="text-left"><strong>Operaciones procesadas</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <span class="measure" id="OperationRate">- op/s</span><br/>
				<strong>Tasa m&aacute;xima:</strong> <span class="measure" id="OperationRatePeak">- op/s</span><br/>
				<strong>Total:</strong> <span class="measure" id="TotalOperations">- op</span><br/>
				<strong>Errores:</strong> <span class="measure" id="TotalOperationsKo">- op</span><br/>
			</p>
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-3">
			<img style="height:80px;" src="<c:url value="/resources/img/workload/orange-flow.png"/>" class="img-responsive" alt="Internet">
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-2">
			<img style="height:100px;" src="<c:url value="/resources/img/workload/database.png"/>" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-8 col-md-offset-0">
			<p class="text-left"><strong>Operaciones enviadas al subsistema</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <span class="measure" id="SubsystemOperationRate">- op/s</span><br/>
				<strong>Tasa m&aacute;xima:</strong> <span class="measure" id="SubsystemOperationRatePeak">- op/s</span><br/>
				<strong>Total:</strong> <span class="measure" id="TotalSubsystemOperations">- op</span><br/>
				<strong>Errores:</strong> <span class="measure" id="TotalSubsystemOpertionsKo">- op</span><br/>
			</p>
		</div>
	</div>
</div>

<script type="text/javascript">

	initSystemWorkload(
			document.getElementById("MeasureStartTime"),
			document.getElementById("RequestsRate"),
			document.getElementById("RequestsRatePeak"),
			document.getElementById("TotalRequests"),
			document.getElementById("TotalRequestsKo"),
			document.getElementById("OperationRate"),
			document.getElementById("OperationRatePeak"),
			document.getElementById("TotalOperations"),
			document.getElementById("TotalOperationsKo"),
			document.getElementById("SubsystemOperationRate"),
			document.getElementById("SubsystemOperationRatePeak"),
			document.getElementById("TotalSubsystemOperations"),
			document.getElementById("TotalSubsystemOpertionsKo")
	);

</script>
