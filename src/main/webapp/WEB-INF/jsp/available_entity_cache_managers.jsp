<%@include file="includes/locations.jsp"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCacheManager"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%!String bodyContent;%>
<%
	List<EntityCacheManager> cacheManagers = (List<EntityCacheManager>)request.getAttribute("cacheManagers");
	StringBuilder output = new StringBuilder();
	
	try {
		if (cacheManagers != null && cacheManagers.size() > 0) {
			
			output.append("<div class=\"panel-body\">");
			output.append("<p>Conjunto de gestores de cach&eacute; configurados en esta instancia de servicio de datos.</p>");
			
			
			Iterator<EntityCacheManager> cacheManagerIterator = cacheManagers.iterator();

			if (cacheManagerIterator.hasNext()) {
				output.append("<table class=\"table\">");
				output.append("<thead><tr>");
				output.append("<th></th>").append("<th>Memoria</th>").append("<th>Disco</th>").append("<th>Ubicaci&oacute;n de los ficheros</th>");
				output.append("</tr></thead>");
				output.append("<tbody>");
				while (cacheManagerIterator.hasNext()) {
					EntityCacheManager  entityCacheManager = cacheManagerIterator.next();
					
					output.append("<tr>");
					output.append("<td>");
					output.append("<a href=\"#\" onclick=\"sendGetRequest('/cache/cacheManagerConfiguration?cacheManager=").append(entityCacheManager.getName()).append("');\">").append(entityCacheManager.getName()).append("</a><br/>");
					output.append("</td>");
					output.append("<td>").append(StorageSize.fromBytesToString(entityCacheManager.getMaxBytesLocalHeap())).append("</td>");
					output.append("<td>").append(StorageSize.fromBytesToString(entityCacheManager.getMaxBytesLocalDisk())).append("</td>");
					output.append("<td>").append(entityCacheManager.getDiskStore().getPath()).append("</td>");
					output.append("</tr>");
				}
				output.append("</tbody>");
				output.append("</table>");
			}
			else {
				output.append("ninguno.");
			}
			output.append("</div>");
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
