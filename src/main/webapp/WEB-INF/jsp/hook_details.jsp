<%@page import="com.fav24.dataservices.service.hook.GenericServiceHook"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%!String bodyContent;%>

<%
	StringBuilder output = new StringBuilder();
	
	try {
		String hookName = (String)request.getAttribute("hook");
		GenericServiceHook hookInstance = (GenericServiceHook)request.getAttribute("hookInstance");

		if (hookInstance == null) {
			
			output.append("El hook <strong>").append(hookName).append("</strong> no existe, o no est&aacute; disponible.");
		} 
		else {
			String hookDescription = hookInstance.getDescription();
			URL hookSourceURL = (URL)request.getAttribute("hookSourceURL");
			BufferedReader reader = new BufferedReader(new InputStreamReader(hookSourceURL.openStream()));
			
			String line;
			StringBuilder sourceCode = null;
			while((line = reader.readLine()) != null) {
				
				if (sourceCode == null) {
					sourceCode = new StringBuilder();
				}
				else {
					sourceCode.append("\n");
				}
				line = line.replace("<","&lt;");
				line = line.replace(">","&gt;");
				sourceCode.append(line);
			}
			reader.close();
			
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
			output.append("<pre class='brush: java; ruler: true;'>");
			if (sourceCode != null) {
				output.append(sourceCode);
			}
			output.append("</pre>");
			output.append("</div>");
			output.append("</div>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<link href="<c:url value="/resources/css/syntaxhighlighter/shCore.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/syntaxhighlighter/shThemeDefault.css"/>" rel="stylesheet" type="text/css" />
<script class="include" type="text/javascript" src="<c:url value="/resources/js/syntaxhighlighter/shCore.min.js"/>"></script>
<script class="include" type="text/javascript" src="<c:url value="/resources/css/syntaxhighlighter/shAutoloader.min.js"/>"></script>

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

<script type="text/javascript">
SyntaxHighlighter.defaults.toolbar = false;
SyntaxHighlighter.autoloader([ 'java', 'java', '<c:url value="/resources/css/syntaxhighlighter/shBrushJava.js"/>']);
SyntaxHighlighter.all();
</script>