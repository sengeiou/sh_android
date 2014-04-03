<%@include file="includes/locations.jsp"%>


<!-- Panel de información del trabajo realizado y en curso. -->
<div id="workload" class="container">
	<div class="page-header">
		<p class="lead">Diagrama de carga de trabajo del sistema</p>
	</div>
	<div class="row">
		<div class="col-md-4 col-md-offset-6">
			<img style="height:120px;" src="<%=imagesURL%>/workload/cloud.png" class="img-responsive" alt="Internet">
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-3">Peticiones por segundo</div>
		<div class="col-md-2 col-md-offset-0">
			<img style="height:80px;" src="<%=imagesURL%>/workload/blue-flow.png" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-2">Peticiones realizadas desde</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-4">
			<img style="height:120px;" src="<%=imagesURL%>/workload/data-server.png" class="img-responsive" alt="Internet">
		</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-1">Peticiones por segundo</div>
		<div class="col-md-2 col-md-offset-0">
			<img style="height:80px;" src="<%=imagesURL%>/workload/orange-flow.png" class="img-responsive" alt="Internet">
		</div>
		<div class="col-md-2">Peticiones realizadas desde</div>
	</div>
	
	<div class="row">
		<div class="col-md-2 col-md-offset-2">
			<img style="height:100px;" src="<%=imagesURL%>/workload/database.png" class="img-responsive" alt="Internet">
		</div>
	</div>
</div>