<%@page import="java.util.List"%>
<%@include file="includes/locations.jsp" %>

<%!String bodyContent;%>
<%
	List<String> entities = (List<String>)request.getAttribute("entities");
	StringBuilder output = new StringBuilder();
	
	try {
		if (entities != null && entities.size() > 0) {
			output.append("<p><b>Entidades p&uacute;blicas:</b>").append("<br/>");
			output.append("<ul>");

			for (String entity : entities) {
				output.append("<li>");
				output.append("<a href=").append('"').append(pagesURL).append("/accesspolicy/entityPolicies?entity=").append(entity).append('"').append(">").append(entity).append("</a><br/>");
				output.append("</li>");
			}
			
			output.append("</ul>");
			output.append("</p>");
		} 
		else {
			output.append("<p>No hay entidades disponibles.</p>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<title>Entidades disponibles</title>
	</head>
	<body class="body-box-shadow">
		<h1>Entidades disponibles</h1>
		<a href="<%=pagesURL%>/index.jsp">
			<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<%=bodyContent%>
	</body>
	<footer>
	</footer>
</html>