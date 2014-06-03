<%@include file="includes/locations.jsp"%>
<%@include file="includes/modal_acceptance.jsp"%>

<!DOCTYPE HTML>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<!-- Definición de los iconos -->
	<link rel="apple-touch-icon" sizes="57x57" href="<%=imagesURL%>/favicons/apple-touch-icon-57x57.png">
	<link rel="apple-touch-icon" sizes="114x114" href="<%=imagesURL%>/favicons/apple-touch-icon-114x114.png">
	<link rel="apple-touch-icon" sizes="72x72" href="<%=imagesURL%>/favicons/apple-touch-icon-72x72.png">
	<link rel="apple-touch-icon" sizes="144x144" href="<%=imagesURL%>/favicons/apple-touch-icon-144x144.png">
	<link rel="apple-touch-icon" sizes="60x60" href="<%=imagesURL%>/favicons/apple-touch-icon-60x60.png">
	<link rel="apple-touch-icon" sizes="120x120" href="<%=imagesURL%>/favicons/apple-touch-icon-120x120.png">
	<link rel="apple-touch-icon" sizes="76x76" href="<%=imagesURL%>/favicons/apple-touch-icon-76x76.png">
	<link rel="apple-touch-icon" sizes="152x152" href="<%=imagesURL%>/favicons/apple-touch-icon-152x152.png">
	<link rel="icon" type="image/png" href="<%=imagesURL%>/favicons/favicon-196x196.png" sizes="196x196">
	<link rel="icon" type="image/png" href="<%=imagesURL%>/favicons/favicon-160x160.png" sizes="160x160">
	<link rel="icon" type="image/png" href="<%=imagesURL%>/favicons/favicon-96x96.png" sizes="96x96">
	<link rel="icon" type="image/png" href="<%=imagesURL%>/favicons/favicon-16x16.png" sizes="16x16">
	<link rel="icon" type="image/png" href="<%=imagesURL%>/favicons/favicon-32x32.png" sizes="32x32">
	<meta name="msapplication-TileColor" content="#da532c">
	<meta name="msapplication-TileImage" content="<%=imagesURL%>/favicons/mstile-144x144.png">

	<title>Servicios de acceso a datos</title>
	
	<!-- Latest compiled and minified CSS -->
	<link class="include" rel="stylesheet" type="text/css" href="<%=cssURL%>/bootstrap.min.css">
	<!-- Optional theme -->
	<link class="include" rel="stylesheet" type="text/css" href="<%=cssURL%>/bootstrap-theme.min.css">
	
	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
      <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script>
      <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script>
    <![endif]-->
</head>

<body role="document">
	<!-- Fixed navbar -->
	<div class="navbar navbar-inverse navbar-fixed-top" role="navigation">
		<div class="container">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
					<span class="sr-only">Toggle navigation</span> 
					<span class="icon-bar"></span> <span class="icon-bar"></span> 
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#" onclick="showWorkload();">Servicios de datos</a>
			</div>
			<div class="navbar-collapse collapse">
				<ul class="nav navbar-nav">
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Principal <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="#" onclick="showDataSourcesInformation();">Informaci&oacute;n de las fuentes de datos</a></li>
							<li><a href="#" onclick="showSystemContext();">Informaci&oacute;n de contexto del sistema</a></li>
							<li class="divider"></li>
							<li><a href="#" onclick="showSystemMonitor();">Informaci&oacute;n de recursos del sistema</a></li>
						</ul>
					</li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Entidades <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="#" onclick="showAvailableEntities();">Entidades publicadas</a></li>
							<li class="divider"></li>
							<li><a href="#" onclick="uploadEntityPolicies();">Carga de pol&iacute;ticas</a></li>
							<li><a href="#" onclick="resetToDefaultPolicies();">Recarga de pol&iacute;ticas por defecto</a></li>
							<li><a href="#" onclick="dropAccessPolicies();">Eliminar pol&iacute;ticas activas</a></li>
						</ul>
					</li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Cach&eacute; <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="#" onclick="">Estado de la cach&eacute;</a></li>
							<li><a href="#" onclick="showAvailableCacheManagers();">Configuraci&oacute;n de la cach&eacute;</a></li>
							<li class="divider"></li>
							<li><a href="#" onclick="uploadCacheConfigutarions();">Carga de configuraciones de cach&eacute;</a></li>
							<li><a href="#" onclick="loadDefaultCacheConfiguration();">Recarga de configuraciones de cach&eacute; por defecto</a></li>
							<li><a href="#" onclick="dropSystemCache();">Eliminar configuraciones de cach&eacute; activas</a></li>
						</ul>
					</li>
					<li class="dropdown"><a href="#" class="dropdown-toggle" data-toggle="dropdown">Hooks <b class="caret"></b></a>
						<ul class="dropdown-menu">
							<li><a href="#" onclick="showAvailableHooks();">Hooks disponibles</a></li>
							<li class="divider"></li>
							<li><a href="#" onclick="uploadHooks();">Carga de Hooks</a></li>
							<li><a href="#" onclick="resetToDefaultHooks();">Recarga de Hooks por defecto</a></li>
							<li><a href="#" onclick="dropAvailableHooks();">Eliminar Hooks disponibles</a></li>
						</ul>
					</li>
				</ul>
			</div>
			<!--/.nav-collapse -->
		</div>
		<!-- Barra del progreso. -->
		<div id="mainProgressBar" class="progress progress-striped active"
			style="height: 5px;">
			<div id="mainProgressBar-bar" class="progress-bar" role="progressbar"
				aria-valuenow="0" aria-valuemin="0" aria-valuemax="100"
				style="width: 0%">
				<span class="sr-only"></span>
			</div>
		</div>
	</div>

	<!-- Contenedor principal. -->
	<div id="mainContent" class="container" style="padding-top: 70px;">
	</div>

	<!-- Bootstrap core JavaScript
	    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script	class="include" type="text/javascript" src="<%=jsURL%>/jquery.min.js"></script>
	<script class="include" type="text/javascript" src="<%=jsURL%>/bootstrap.min.js"></script>

	<script type="text/javascript">

		var App = new Object();
	
		App.baseURL = "<%=baseURL%>";
		App.servicesURL = "<%=servicesURL%>";
		App.pagesURL = "<%=pagesURL%>";
		App.imagesURL = "<%=imagesURL%>";
		App.cssURL = "<%=cssURL%>";
		App.jsURL = "<%=jsURL%>";
		// Asignación de la función de destrución de los procesos activos de una determinada página.
		App.destructionFunction = null;

		// Ejecución después de la carga del documento.
		$(document).ready(function(){
			showWorkload();
		});

	</script>
	
	<script class="include" type="text/javascript" src="<%=jsURL%>/main.js"></script>
	
</body>
<footer>
</footer>
</html>