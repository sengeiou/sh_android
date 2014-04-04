<%@include file="includes/locations.jsp"%>

<!-- Panel de información del trabajo realizado y en curso. -->
<div id="workload" class="container-fluid ">
	<div class="page-header">
		<p class="lead">Diagrama de carga de trabajo del sistema</p>
	</div>
	<div class="row">
		<div class="col-md-2 col-md-offset-6">
			<img style="height:120px;" src="<%=imagesURL%>/workload/cloud.png" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-4 col-md-offset-0">
			<p class="text-left"><strong>Peticiones entrantes</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <a id="RequestsRate">- req/s</a><br/>
				<strong>Tasa m&aacute;xima:</strong> <a id="RequestsRatePeak">- req/s</a><br/>
				<strong>Total:</strong> <a id="TotalRequests">- req</a><br/>
			</p>
		</div>
	</div>

	<div class="row">
		<div class="col-md-2 col-md-offset-5">
			<img style="height:80px;" src="<%=imagesURL%>/workload/blue-flow.png" class="img-responsive" alt="Internet">
		</div>
	</div>

	<div class="row">
		<div class="col-md-2 col-md-offset-4">
			<img style="height:120px;" src="<%=imagesURL%>/workload/data-server.png" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-6 col-md-offset-0">
			<p class="text-left"><strong>Operaciones procesadas</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <a id="OperationRate">- op/s</a><br/>
				<strong>Tasa m&aacute;xima:</strong> <a id="OperationRatePeak">- op/s</a><br/>
				<strong>Total:</strong> <a id="TotalOperations">- op</a><br/>
				<strong>Errores:</strong> <a id="TotalOperationsKo">- op</a><br/>
			</p>
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-3">
			<img style="height:80px;" src="<%=imagesURL%>/workload/orange-flow.png" class="img-responsive" alt="Internet">
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-2">
			<img style="height:100px;" src="<%=imagesURL%>/workload/database.png" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-8 col-md-offset-0">
			<p class="text-left"><strong>Operaciones enviadas al subsistema</strong></p>
			<p class="text-left text-muted">
				<strong>Tasa:</strong> <a id="SubsystemOperationRate">- op/s</a><br/>
				<strong>Tasa m&aacute;xima:</strong> <a id="SubsystemOperationRatePeak">- op/s</a><br/>
				<strong>Total:</strong> <a id="TotalSubsystemOperations">- op</a><br/>
				<strong>Errores:</strong> <a id="TotalSubsystemOpertionsKo">- op</a><br/>
			</p>
		</div>
	</div>
</div>