<%@page import="com.fav24.dataservices.security.EntityAccessPolicy"%>
<%@include file="includes/locations.jsp" %>

<%!String bodyContent;%>
<%!String entity; %>
<%
	StringBuilder output = new StringBuilder();
	
	try {
		entity = (String)request.getAttribute("entity");
		EntityAccessPolicy entityPolicies = (EntityAccessPolicy)request.getAttribute("entityPolicies");

		if (entityPolicies == null) {
			
			output.append("La entidad <b>").append(entity)
			.append("</b> no existe, o no es accesible.");
		} else {

			output.append("<p>");
			output.append("Detalle de las pol&iacute;ticas de acceso<br/>");
			output.append(entityPolicies.getDetails(true));
			output.append("</p>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<!DOCTYPE HTML>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<title>Pol&iacute;ticas de acceso de la entidad ${entity}</title>
	</head>
	<body>
		<h1>Pol&iacute;ticas de acceso</h1>
		<a href="<%=pagesURL%>/index.jsp">
			<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<h2>Entidad ${entity}</h2>
		<%=bodyContent%>
	</body>
	<footer>
	</footer>
</html>