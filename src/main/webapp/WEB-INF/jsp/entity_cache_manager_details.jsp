<%@page import="java.util.Set"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCacheManager"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCache"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%!String bodyContent;%>
<%
	StringBuilder output = new StringBuilder();
	
	try {
		EntityCacheManager entityCacheManager = (EntityCacheManager)request.getAttribute("cacheManagerConfiguration");

		if (entityCacheManager == null) {
			
			output.append("El gestor de cach&eacute; <strong>").append((String)request.getAttribute("cacheManager")).append("</strong> no existe.");
		} 
		else {
			output.append("<p>Detalle de la configuraci&oacute;n<p/>");

			// Límites de almacenamiento del gestor de caché.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>L&iacute;mites de alacenamiento</div>");
			output.append("<div class='panel-body'>");
			output.append("<ul class='list-group'>");
			output.append("<li class='list-group-item'>").append("<strong>Memoria:</strong> ").append(StorageSize.fromBytesToString(entityCacheManager.getMaxBytesLocalHeap())).append("</li>");
			output.append("<li class='list-group-item'>").append("<strong>Disco:</strong> ").append(StorageSize.fromBytesToString(entityCacheManager.getMaxBytesLocalDisk())).append("</li>");
			output.append("</ul>");
			output.append("</div>");
			output.append("</div>");
			
			// Ubicación de los ficheros de caché en disco.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Ubicaci&oacute;n de los ficheros de cach&eacute; en disco</div>");
			output.append("<div class='panel-body'>");
			output.append("<p>").append(entityCacheManager.getDiskStore().getPath()).append("</p>");
			output.append("</div>");
			output.append("</div>");

			Set<EntityCache> entityCacheConfigurations = entityCacheManager.getEntitiesCacheConfigurations();
			if (entityCacheConfigurations != null && entityCacheConfigurations.size() > 0) {
				
				output.append("<div class=\"panel-body\">");
				output.append("<p>Conjunto de cach&eacute;s de entidades.</p>");
				output.append("</div>");
				
				Iterator<EntityCache> entityCacheConfigurationsIterator = entityCacheConfigurations.iterator();

				if (entityCacheConfigurationsIterator.hasNext()) {
					output.append("<table class=\"table\">");
					output.append("<thead><tr>");
					output.append("<th></th>").append("<th>Memoria</th>").append("<th>%</th>").append("<th>Disco</th>").append("<th>%</th>");
					output.append("</tr></thead>");
					output.append("<tbody>");
					while (entityCacheConfigurationsIterator.hasNext()) {
						EntityCache entityCacheConfiguration = entityCacheConfigurationsIterator.next();
						
						Double heapProportion = (Double.valueOf(entityCacheConfiguration.getMaxBytesLocalHeap()) / Double.valueOf(entityCacheManager.getMaxBytesLocalHeap())) * 100;
						Double discProportion = (Double.valueOf(entityCacheConfiguration.getMaxBytesLocalDisk()) / Double.valueOf(entityCacheManager.getMaxBytesLocalDisk())) * 100;
						
						output.append("<tr>");
						output.append("<td>");
						output.append("<a href=\"#\" onclick=\"sendGetRequest('/cache/cacheConfiguration?").
								append("cacheManager=").append(entityCacheManager.getName()).append('&').
								append("entity=").append(entityCacheConfiguration.getAlias()).
								append("');\">").append(entityCacheConfiguration.getAlias()).append("</a><br/>");
						output.append("</td>");
						output.append("<td>").append(StorageSize.fromBytesToString(entityCacheConfiguration.getMaxBytesLocalHeap())).append("</td>");
						output.append("<td>").append(heapProportion).append("</td>");
						output.append("<td>").append(StorageSize.fromBytesToString(entityCacheConfiguration.getMaxBytesLocalDisk())).append("</td>");
						output.append("<td>").append(discProportion).append("</td>");
						output.append("</tr>");
					}
					output.append("</tbody>");
					output.append("</table>");
				}
				else {
					output.append("Este gestor de cach&eacute;s no contiene ninguna cach&eacute; de entidad definida.");
				}
			} 
			else {
				output.append("<div class=\"panel-body\">");
				output.append("<p>No ning&uacute;n gestor de cach&eacute; configurado.</p>");
				output.append("</div>");
			}
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<div class="panel-heading"><h3>${cacheManager}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<%=bodyContent%>
		</div>
	</div>
</div>
