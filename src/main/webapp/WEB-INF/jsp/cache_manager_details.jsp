<%@page import="com.fav24.dataservices.domain.security.EntityAccessPolicy"%>
<%@page import="com.fav24.dataservices.domain.security.EntityAccessPolicy.OperationType"%>
<%@page import="com.fav24.dataservices.domain.security.EntityAttribute"%>
<%@page import="com.fav24.dataservices.domain.security.EntityDataAttribute"%>
<%@page import="com.fav24.dataservices.domain.security.EntityDataAttribute.Direction"%>
<%@page import="com.fav24.dataservices.domain.security.EntityKey"%>
<%@page import="com.fav24.dataservices.domain.security.EntityFilter"%>
<%@page import="java.util.Iterator"%>
<%@include file="includes/locations.jsp" %>

<%!String bodyContent;%>
<%!String entity;%>
<%
	StringBuilder output = new StringBuilder();
	
	try {
		entity = (String)request.getAttribute("entity");
		EntityAccessPolicy entityPolicies = (EntityAccessPolicy)request.getAttribute("entityPolicies");

		if (entityPolicies == null) {
			
			output.append("La entidad <strong>").append(entity).append("</strong> no existe, o no es accesible.");
		} 
		else {
			output.append("<p>Detalle de las pol&iacute;ticas de acceso<p/>");

			// Operaciones permitidas.
			output.append("<div class=\"panel panel-info\">");
			output.append("<div class=\"panel-heading\">Operaciones permitidas</div>");
			output.append("<div class=\"panel-body\">");
			Iterator<OperationType> operations = entityPolicies.getAllowedOperations().iterator();
			if (operations.hasNext()) {
				output.append(operations.next());
				while (operations.hasNext()) {
					output.append(',').append(operations.next());
				}
			}
			else {
				output.append("ninguna");
			}
			output.append("</div>");
			output.append("</div>");

			// Dirección de los atributos.
			output.append("<div class=\"panel panel-info\">");
			output.append("<div class=\"panel-heading\">Atributos</div>");
			output.append("<div class=\"panel-body\">");

			Iterator<EntityDataAttribute> attributeIterator = entityPolicies.getData().getData().iterator();

			if (attributeIterator.hasNext()) {
				output.append("<table class=\"table\">");
				output.append("<thead><tr>");
				output.append("<th></th>").append("<th>Alias</th>").append("<th>Nombre en la fuente</th>");
				output.append("</tr></thead>");
				output.append("<tbody>");
				while (attributeIterator.hasNext()) {
					EntityDataAttribute  attribute = attributeIterator.next();
					
					output.append("<tr>");
					output.append("<td>");
					output.append("<img class=\"img-polaroid\" width=\"16px\" height=\"16px\" src=\"").append(imagesURL);
					if (attribute.getDirection() == Direction.INPUT) {
						output.append("/red-input_64x64.png");
					}
					else if (attribute.getDirection() == Direction.OUTPUT) {
						output.append("/blue-output_64x64.png");
					}
					else if (attribute.getDirection() == Direction.BOTH) {
						output.append("/input_output_arrows_64x64.png");
					}
					output.append("\" alt=\"\"/>");
					output.append("</td>");
					output.append("<td>").append(attribute.getAlias()).append("</td>");
					output.append("<td>").append(attribute.getName()).append("</td>");
					output.append("</tr>");
				}
				output.append("</tbody>");
				output.append("</table>");
			}
			else {
				output.append("ninguno.");
			}
			output.append("</div>");
			output.append("</div>");

			// Keys disponibles.
			output.append("<div class=\"panel panel-info\">");
			output.append("<div class=\"panel-heading\">Claves</div>");

			output.append("<div class=\"panel-body\">");
			
			if (entityPolicies.getOnlyByKey()) {
				output.append("<p><b>Importante: </b>");
				output.append("esta entidad &uacute;nicamente es accesible mediate el uso de una de las claves especificadas a continuaci&oacute;n.");	
			}
		
			if (entityPolicies.getKeys() != null && entityPolicies.getKeys().getKeys().size() > 0) {

				Iterator<EntityKey> keysIterator = entityPolicies.getKeys().getKeys().iterator();

				output.append("<ul class=\"list-group\">");
				while (keysIterator.hasNext()) {

					output.append("<li class=\"list-group-item\">");
					Iterator<EntityAttribute> keyIterator = keysIterator.next().getKey().iterator();

					if (keyIterator.hasNext()) {
						output.append(keyIterator.next().getAlias());
						while (keyIterator.hasNext()) {
							EntityAttribute key = keyIterator.next();
							output.append(", ").append(key.getAlias());
						}
					}
					output.append("</li>");
				}
				output.append("</ul>");
			}
			else {
				output.append("ninguna.");
			}

			output.append("</div>");
			output.append("</div>");

			// Filtros disponibles.
			output.append("<div class=\"panel panel-info\">");
			output.append("<div class=\"panel-heading\">Filtros</div>");

			output.append("<div class=\"panel-body\">");
			
			if (entityPolicies.getOnlySpecifiedFilters()) {
				output.append("<p><b>Importante: </b>");
				output.append("esta entidad &uacute;nicamente es accesible mediate el uso de alguno de los filtros especificados a continuaci&oacute;n.");	
			}
		
			if (entityPolicies.getFilters() != null && entityPolicies.getFilters().getFilters().size() > 0) {

				Iterator<EntityFilter> filtersIterator = entityPolicies.getFilters().getFilters().iterator();

				output.append("<ul class=\"list-group\">");
				while (filtersIterator.hasNext()) {

					output.append("<li class=\"list-group-item\">");
					Iterator<EntityAttribute> filterIterator = filtersIterator.next().getFilter().iterator();

					if (filterIterator.hasNext()) {
						output.append(filterIterator.next().getAlias());
						while (filterIterator.hasNext()) {
							EntityAttribute filter = filterIterator.next();
							output.append(", ").append(filter.getAlias());
						}
					}
					output.append("</li>");
				}
				output.append("</ul>");
			}
			else {
				output.append("ninguno.");
			}

			output.append("</div>");
			output.append("</div>");
			
			output.append("<br/><b>Tama&ntilde;o m&aacute;ximo de p&aacute;gina: </b>");
			output.append(entityPolicies.getMaxPageSize());
			output.append("</p>");
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
