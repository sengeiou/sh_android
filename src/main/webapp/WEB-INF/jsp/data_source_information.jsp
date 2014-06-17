<!-- Panel de información de las fuentes de datos implicadas. -->
<div id="dataSources">

	<!-- Fuente de datos que se sirve -->
	<div class="panel panel-info">
		<div class="panel-heading">Fuente de datos</div>
		<div class="panel-body">
			<p>Esta es la fuentes de datos que esta instancia est&aacute; publicando en estos momentos.</p>
		</div>
		
		<!-- Atributos de la instancia -->
		<ul class="list-group">
			<li class="list-group-item"><Strong>Servidor: </Strong>${dataSource.get("host")}</li>
			<li class="list-group-item"><Strong>Puerto: </Strong>${dataSource.get("port") == null ? "default" : dataSource.get("port")}</li>
			<li class="list-group-item"><Strong>Versi&oacute;n: </Strong>${dataSource.get("databaseVersion")}</li>
			<li class="list-group-item"><Strong>Ruta: </Strong>${dataSource.get("path")}</li>
			<li class="list-group-item"><Strong>Usuario: </Strong>${dataSource.get("user")}</li>
			<li class="list-group-item"><Strong>Cadena de conexi&oacute;n: </Strong>${dataSource.get("url")}</li>
		</ul>
	</div>

	<!-- Fuente de datos estadísticos -->
	<div class="panel panel-info">
		<div class="panel-heading">Ubicaci&oacute;n de la informaci&oacute;n de uso</div>
		<div class="panel-body">
			<p>Esta es la fuente de datos en que se almacena la informaci&oacute;n de uso de esta instancia.</p>
		</div>
		
		<!-- Atributos de la instancia -->
		<ul class="list-group">
			<li class="list-group-item"><Strong>Servidor: </Strong>${statsDataSource.get("host")}</li>
			<li class="list-group-item"><Strong>Puerto: </Strong>${statsDataSource.get("port") == null ? "default" : statsDataSource.get("port")}</li>
			<li class="list-group-item"><Strong>Versi&oacute;n: </Strong>${statsDataSource.get("databaseVersion")}</li>
			<li class="list-group-item"><Strong>Ruta: </Strong>${statsDataSource.get("path")}</li>
			<li class="list-group-item"><Strong>Usuario: </Strong>${statsDataSource.get("user")}</li>
			<li class="list-group-item"><Strong>Cadena de conexi&oacute;n: </Strong>${statsDataSource.get("url")}</li>
		</ul>
	</div>
</div>