<%@include file="includes/locations.jsp" %>
<%@page import="java.util.Iterator"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCacheManager"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCache"%>
<%@page import="com.fav24.dataservices.domain.cache.CacheConfiguration.MemoryStoreEvictionPolicy"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%!String bodyContent;%>
<%
	StringBuilder output = new StringBuilder();
	
	try {
		EntityCacheManager entityCacheManager = (EntityCacheManager)request.getAttribute("cacheManagerConfiguration");
		EntityCache entityCache = (EntityCache)request.getAttribute("cacheConfiguration");

		if (entityCache == null || entityCacheManager == null) {
			
			output.append("No ha sido posible localizar la cach&eacute; <strong>").append((String)request.getAttribute("entity")).append("</strong> en el gestor <strong>").
			append((String)request.getAttribute("cacheManager")).append("</strong>.");
		} 
		else {
			output.append("<p>Detalle de la configuraci&oacute;n<p/>");

			// Caducidad.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Caducidad de los elementos</div>");
			output.append("<div class='panel-body'>");
			output.append("<ul class='list-group'>");

			output.append("<li class='list-group-item'>").append("<strong>Eternos: </strong> ").append(entityCache.getExpiry().isEternal() ? "S&iacute;" : "No").append("</li>");
			String liStyle;
			if (entityCache.getExpiry().isEternal()) {
				liStyle = "<li class='list-group-item' style='opacity:0.6;'>";
			}
			else {
				liStyle = "<li class='list-group-item'>";
			}

			output.append(liStyle).append("<strong>Tiempo m&aacute;ximo sin ser consultados en segundos: </strong> ").append(entityCache.getExpiry().getTimeToIdleSeconds()).append("</li>");
			output.append(liStyle).append("<strong>Tiempo m&aacute;ximo de vida en segundos: </strong> ").append(entityCache.getExpiry().getTimeToLiveSeconds()).append("</li>");
			output.append("<li class='list-group-item'>").append("<strong>Intervalo en segundos entre chequeos de caducidad de los elementos en disco: </strong> ").append(entityCache.getDiskExpiryThreadIntervalSeconds()).append("</li>");
			output.append("</ul>");
			output.append("</div>");
			output.append("</div>");

			// Límites de almacenamiento.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>L&iacute;mites de alacenamiento</div>");
			output.append("<div class='panel-body'>");
			output.append("<ul class='list-group'>");
			output.append("<li class='list-group-item'>").append("<strong>Memoria:</strong> ").append(StorageSize.fromBytesToString(entityCache.getMaxBytesLocalHeap())).append("</li>");
			output.append("<li class='list-group-item'");
			if (!entityCache.getPersistente().isActive()) {
				output.append(" style='opacity:0.6;'");
			}
			output.append(">").append("<strong>Disco:</strong> ").append(StorageSize.fromBytesToString(entityCache.getMaxBytesLocalDisk())).append("</li>");
			output.append("<li class='list-group-item'>").append("<strong>Persistencia:</strong> ").append(entityCache.getPersistente().isActive() ? "S&iacute;" : "No").append("</li>");
			output.append("</ul>");
			
			// Respecto al manager que contiene esta caché.
			
			output.append("</div>");
			output.append("</div>");
			
			// Política de desalojo del heap.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Pol&iacute;ca de desalojo de la memoria</div>");
			output.append("<div class='panel-body'>");
			output.append("<p>");
			
			if (MemoryStoreEvictionPolicy.LRU.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desaloja de memoria, el elemento que hace m&aacute;s tiempo que no se consulta. LRU (Least Recently Used)");
			}
			else if (MemoryStoreEvictionPolicy.LFU.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desaloja de memoria, el elemento que se usa menos frecuentemente. LFU (Least Frequently Used)");
			}
			else if (MemoryStoreEvictionPolicy.FIFO.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desalojan los elemento de memoria, en el mismo orden de entrada en la cach&eacute; de los elementos. FIFO (First In First Out)");
			}
			
			output.append("</p>");
			output.append("</div>");
			output.append("</div>");

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
		<div class="panel-heading"><h3>${entity}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<%=bodyContent%>
		</div>
	</div>
</div>
