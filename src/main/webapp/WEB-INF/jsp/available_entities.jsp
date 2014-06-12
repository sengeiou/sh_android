<%@page import="java.util.List"%>

<%!String bodyContent;%>
<%
	List<String> entities = (List<String>)request.getAttribute("entities");
	List<String> virtualEntities = (List<String>)request.getAttribute("virtualEntities");
	StringBuilder output = new StringBuilder();
	
	try {
		if (entities != null && entities.size() > 0) {
			
			output.append("<div class=\"panel-body\">");
			output.append("<p>Estas son las entidades de datos que esta instancia est&aacute; ofreciendo en estos momentos.</p>");
			output.append("</div>");
		
			output.append("<div class=\"list-group\">");

			for (String entity : entities) {
				
				output.append("<a class=\"list-group-item");
				
				if (virtualEntities.contains(entity)) {
					output.append(" list-group-item-warning");
				}

				output.append("\" href=\"#\" onclick=\"sendGetRequest('/accesspolicy/entityPolicies?entity=").append(entity).append("');\">").append(entity).append("</a>");
			}
			
			output.append("</div>");
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
