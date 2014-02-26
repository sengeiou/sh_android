<%@page import="org.springframework.web.context.support.*"%>
<%@page import="org.springframework.web.context.*"%>
<%@page import="com.fav24.dataservices.domain.security.*"%>
<%@page import="com.fav24.dataservices.service.security.*"%>
<%@include file="includes/locations.jsp" %>

<!DOCTYPE HTML>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<title>Servicios de acceso a datos</title>
	</head>
	<body>
		<h1>Men&uacute; principal</h1>
		<h2>Servicios de acceso a datos</h2>
		<div class="scrollPane" style="border: 1px solid; border-color: black;" id="scroll_1">
			<div class="scrollContent">
				<table style="padding-left: 40px; padding-top: 0px;">
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/entity.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/availableEntities'>Listado de entidades disponibles.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/policy-folder.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Carga de ficheros de pol&iacute;ticas de acceso.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/chart.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Estado de la cach&eacute; del sistema.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/chart-folder.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/accessPolicyUpload.show'>Estado de la cach&eacute; del sistema.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/reload.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/loadDefault'>Carga las pol&iacute;ticas de acceso por defecto.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/lock.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/denyAll'>Deniega cualquier acceso <strong>con efecto inmediato</strong>.</a></td>
					</tr>
					<tr>
						<td style="border: none; margin: 0px; padding: 10px;"><img height="64px" width="64px" src="<%=imagesURL%>/sample.png" style="border: none;"></td>
						<td style="padding-left: 20px;"><a href='<%=pagesURL%>/accesspolicy/availableEntities'>Ejemplos de llamadas a servicios.</a></td>
					</tr>
				</table>
			</div>
		</div>
	</body>
	<footer>
	</footer>
</html>