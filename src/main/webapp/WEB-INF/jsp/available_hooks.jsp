<%@page import="java.util.NavigableMap"%>
<%@page import="java.util.Map.Entry"%>
<%@page import="com.fav24.dataservices.service.hook.GenericServiceHook"%>

<%!String bodyContent;%>
<%
	NavigableMap<String, GenericServiceHook> hooks = (NavigableMap<String, GenericServiceHook>)request.getAttribute("hooks");
	StringBuilder output = new StringBuilder();
	
	try {
		if (hooks != null && hooks.size() > 0) {
			
			output.append("<div class=\"panel-body\">");
			output.append("<p>Estos son los puntos de inserci&oacute;n (hooks) disponibles en estos momentos.</p>");
			output.append("</div>");
		
			output.append("<div class=\"list-group\">");

			for (Entry<String, GenericServiceHook> hook : hooks.entrySet()) {
				output.append("<a class=\"list-group-item\" href=\"#\" onclick=\"sendGetRequest('hook/hookDetails?hook=").append(hook.getKey()).append("');\">").append(hook.getKey()).append("</a>");
			}
			
			output.append("</div>");
		} 
		else {
			output.append("<div class=\"panel-body\">");
			output.append("<p>No hay hooks disponibles.</p>");
			output.append("</div>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<!-- Panel de información de los hooks cargados. -->
<div id="availableHooks">

	<!-- Hooks cargados -->
	<div class="panel panel-info">
		<div class="panel-heading">Hooks disponibles</div>
		<!-- Lista de hooks -->
		<%=bodyContent%>
	</div>
</div>
