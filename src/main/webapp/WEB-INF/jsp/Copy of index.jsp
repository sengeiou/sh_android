<%@include file="includes/locations.jsp"%>

<!DOCTYPE HTML>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<meta name="description" content="">
	<meta name="author" content="">
	<link rel="shortcut icon" href="<%=imagesURL%>/favicon.ico">
	
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
          <a class="navbar-brand" href="#">Bootstrap theme</a>
        </div>
        <div class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
            <li class="active"><a href="#">Home</a></li>
            <li><a href="#about">About</a></li>
            <li><a href="#contact">Contact</a></li>
            <li class="dropdown">
              <a href="#" class="dropdown-toggle" data-toggle="dropdown">Dropdown <b class="caret"></b></a>
              <ul class="dropdown-menu">
                <li><a href="#">Action</a></li>
                <li><a href="#">Another action</a></li>
                <li><a href="#">Something else here</a></li>
                <li class="divider"></li>
                <li class="dropdown-header">Nav header</li>
                <li><a href="#">Separated link</a></li>
                <li><a href="#">One more separated link</a></li>
              </ul>
            </li>
          </ul>
        </div><!--/.nav-collapse -->
      </div>
    </div>
    
	<h1>Men&uacute; principal</h1>
	<h2>Servicios de acceso a datos</h2>
	<div class="scrollPane" style="border: 1px solid; border-color: black;"
		id="scroll_1">
		<div class="scrollContent">
			<table style="padding-left: 40px; padding-top: 0px;">
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/entity.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/availableEntities'>Listado de
							entidades disponibles.</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/policy-folder.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Carga
							de ficheros de pol&iacute;ticas de acceso.</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/chart.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Estado
							de la cach&eacute; del sistema.</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/chart-folder.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Estado
							de la cach&eacute; del sistema.</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/reload.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/loadDefault'>Carga las
							pol&iacute;ticas de acceso por defecto.</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/lock.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/denyAll'>Deniega cualquier
							acceso <strong>con efecto inmediato</strong>.
					</a></td>
				</tr>
				<tr>
					<td style="border: none; margin: 0px; padding: 10px;"><img
						height="64px" width="64px" src="<%=imagesURL%>/sample.png"
						style="border: none;"></td>
					<td style="padding-left: 20px;"><a
						href='<%=pagesURL%>/accesspolicy/availableEntities'>Ejemplos
							de llamadas a servicios.</a></td>
				</tr>
			</table>
		</div>
	</div>

	<!-- Bootstrap core JavaScript
	    ================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->
	<script	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.0/jquery.min.js"></script>
	<script src="<%=jsURL%>/bootstrap.min.js"></script>
</body>
<footer> 
</footer>
</html>