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
	<link rel="stylesheet" href="<%=cssURL%>/bootstrap.min.css">
	<!-- Optional theme -->
	<link rel="stylesheet" href="<%=cssURL%>/bootstrap-theme.min.css">
	
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
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </button>
          <a class="navbar-brand" href="#">Servicios de datos</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#" onclick="loadContent('dataSourceInformation');">Principal</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Entidades <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="#" onclick="loadContent('accesspolicy/availableEntities');">Entidades publicadas</a></li>
                <li><a href="#" onclick="loadContent('accesspolicy/accessPolicyUpload.show');">Carga de pol&iacute;ticas</a></li>
                <li class="divider"></li>
                <li>
                	<a href="#" onclick="
	                modalAcceptance(
	                	'&iquest;Desea recargar las pol&iacute;ticas de acceso por defecto?', 
	                	'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la p&eacute;rdida de todos los cambios aplicados manualmente, mediante la carga de ficheros de pol&iacute;ticas de acceso.',
	                	function(){ window.location = '<%=pagesURL%>/accesspolicy/loadDefault'}
	                ); 
	                return false;">Recarga de pol&iacute;ticas por defecto</a>
	            </li>
                <li>
	                <a href="#" onclick="
	                modalAcceptance(
	                	'&iquest;Desea eliminar pol&iacute;ticas activas?', 
	                	'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; el <strong>bloqueo total</strong> del acceso a los servicios de datos.',
	                	function(){ window.location = '<%=pagesURL%>/accesspolicy/denyAll'}
	                ); 
	                return false;">Eliminar pol&iacute;ticas activas</a>
                </li>
              </ul>
            </li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Cach&eacute; <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="#">Informaci&oacute;n de recursos del sistema</a></li>
                <li><a href="#">Informaci&oacute;n de la cach&eacute;</a></li>
                <li><a href="#">Carga de configuraciones de cach&eacute;</a></li>
                <li class="divider"></li>
                <li><a href="#">Recarga de configuraciones de cach&eacute; por defecto</a></li>
                <li><a href="#">Eliminar configuraciones de cach&eacute; activas</a></li>
              </ul>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
	
	<!-- Contenedor principal. -->
	<div id="mainContent" class="container" style="padding-top: 70px;">
	</div>

	<!-- Bootstrap core JavaScript
	    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script	src="<%=jsURL%>/jquery.min.js"></script>
	<script src="<%=jsURL%>/bootstrap.min.js"></script>

	<!-- Función de carga de contenido -->
	<script type="text/javascript">
	
		function loadContent(page) {
			
			var contentObject = $("#mainContent");
			
			contentObject.load("<%=pagesURL%>/" + page);
		};
	</script>
	
	<!-- Ejecución después de la carga del documento. -->
	<script type="text/javascript">
		$(document).ready(function(){
			loadContent('dataSourceInformation');
		});
	</script>
</body>
<footer> 
</footer>
</html>