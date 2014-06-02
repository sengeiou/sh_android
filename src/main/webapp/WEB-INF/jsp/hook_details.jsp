<%@page import="com.fav24.dataservices.service.hook.GenericServiceHook"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Iterator"%>
<%@include file="includes/locations.jsp" %>

<%!String bodyContent;%>
<%!String hookName;%>
<%!String hookDescription;%>
<%!String hookSource;%>

<%
	StringBuilder output = new StringBuilder();
	
	try {
		hookName = (String)request.getAttribute("hook");
		GenericServiceHook hookInstance = (GenericServiceHook)request.getAttribute("hookInstance");

		if (hookInstance == null) {
			
			output.append("El hook <strong>").append(hookName).append("</strong> no existe, o no est&aacute; disponible.");
		} 
		else {
			hookDescription = hookInstance.getDescription();
			URL hookSourceURL = (URL)request.getAttribute("hookSourceURL");
			
			output.append("<p>Detalle del punto de acceso (hook).<p/>");

			// Nombre y descripción del hook.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Identificaci&oacute;n</div>");
			output.append("<div class='panel-body'>");
			
			output.append("<ul class='list-group'>");
			output.append("<li class='list-group-item'><Strong>Alias: </Strong>").append(hookName).append("</li>");
			output.append("<li class='list-group-item'><Strong>Descripci&oacute;n: </Strong>").append(hookDescription).append("</li>");
			output.append("</ul>");

			output.append("</div>");
			output.append("</div>"); 

			// Previsualización del fuente.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Fuente</div>");
			output.append("<div class='panel-body'>");

			output.append("</div>");
			output.append("</div>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<script class="include" type="text/javascript" src="<%=jsURL%>/syntaxhighlighter/shBrushJava.js"></script>

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
