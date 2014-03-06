<%@include file="includes/locations.jsp"%>
<%@page import="java.util.List"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCacheManager"%>

<%!String bodyContent;%>
<%
	List<EntityCacheManager> cacheManagers = (List<EntityCacheManager>)request.getAttribute("cacheManagers");
	StringBuilder output = new StringBuilder();
	
	try {
		if (cacheManagers != null && cacheManagers.size() > 0) {
			
			output.append("<div class=\"panel-body\">");
			output.append("<p>A continuaci&oacute; se muestra el conjunto de gestores de cach&eacute; confurados en esta instancia de servicio de datos.</p>");
			output.append("</div>");
		
			output.append("<ul class=\"list-group\">");

			for (EntityCacheManager cacheManager : cacheManagers) {
				output.append("<li class=\"list-group-item\">");
				output.append("<a href=\"#\" onclick=\"sendGetRequest('/cache/cacheManagerConfiguration?cacheManager=").append(cacheManager.getName()).append("');\">").append(cacheManager.getName()).append("</a><br/>");
				output.append("</li>");
			}
			
			output.append("</ul>");
		} 
		else {
			output.append("<div class=\"panel-body\">");
			output.append("<p>No ning&uacute;n gestor de cach&eacute; configurado.</p>");
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
		<div class="panel-heading">Gestores de cach&eacute; disponibles</div>
		<!-- Lista de entidades -->
		<%=bodyContent%>
	</div>
</div>
