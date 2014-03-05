<%@include file="includes/locations.jsp"%>
<%@page import="java.util.List"%>

<%!String bodyContent;%>
<%
	List<String> cacheManagers = (List<String>)request.getAttribute("cacheManagers");
	StringBuilder output = new StringBuilder();
	
	try {
		if (cacheManagers != null && cacheManagers.size() > 0) {
			
			output.append("<div class=\"panel-body\">");
			output.append("<p>Estas son la entidades de datos que esta instancia est&aacute; ofreciendo en estos momentos.</p>");
			output.append("</div>");
		
			output.append("<ul class=\"list-group\">");

			for (String cacheManager : cacheManagers) {
				output.append("<li class=\"list-group-item\">");
				output.append("<a href=\"#\" onclick=\"sendGetRequest('/cache/entityPolicies?cacheManager=").append(cacheManager).append("');\">").append(cacheManager).append("</a><br/>");
				output.append("</li>");
			}
			
			output.append("</ul>");
		} 
		else {
			output.append("<div class=\"panel-body\">");
			output.append("<p>No hay entidades disponibles.</p>");
			output.append("</div>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<!-- Panel de información de las entidades publicadas. -->
<div id="availableEntities">

	<!-- Entidades disponibles -->
	<div class="panel panel-info">
		<div class="panel-heading">Entidades disponibles</div>
		<!-- Lista de entidades -->
		<%=bodyContent%>
	</div>
</div>
