<%@page import="com.fav24.dataservices.service.hook.GenericServiceHook"%>
<%@page import="java.net.URL"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%!String sourceCode;%>

<%
	try {
		URL hookSourceURL = (URL)request.getAttribute("hookSourceURL");
		if (hookSourceURL != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(hookSourceURL.openStream()));
			
			String line;
			StringBuilder sourceCodeLines = null;
			while((line = reader.readLine()) != null) {
				
				if (sourceCodeLines == null) {
					sourceCodeLines = new StringBuilder();
				}
				else {
					sourceCodeLines.append("\n");
				}
				line = line.replace("<","&lt;");
				line = line.replace(">","&gt;");
				sourceCodeLines.append(line);
			}
			reader.close();
			sourceCode = sourceCodeLines.toString();
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
%>

<link href="<c:url value="/resources/css/syntaxhighlighter/shCore.css"/>" rel="stylesheet" type="text/css" />
<link href="<c:url value="/resources/css/syntaxhighlighter/shThemeDefault.css"/>" rel="stylesheet" type="text/css" />

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick='showAvailableHooks();' type='button' class='btn btn-default btn-sm pull-right'>
   			<span class='glyphicon glyphicon-th-list'></span>
   		</button>
		<div class="panel-heading">
    		<h3>${hook}</h3>
		</div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty hookInstance}">
					El hook <strong>${hook}</strong> no existe, o no est&aacute; disponible.
				</c:when>
				<c:otherwise>
					<p>Detalle del punto de acceso (hook).<p/>
					<!-- Nombre y descripción del hook.  -->
					<br/>
					<div class="panel panel-info">
						<div class="panel-heading">Identificaci&oacute;n</div>
						<div class="panel-body">
							<br/>
							<ul class="list-group">
								<li class="list-group-item"><Strong>Alias: </Strong>${hook}</li>
								<li class="list-group-item"><Strong>Descripci&oacute;n: </Strong>${hookInstance.getDescription()}</li>
							</ul>
						</div>
					</div> 
		
					<!-- Previsualización del fuente. -->
					<div class="panel panel-info">
						<div class="panel-heading">Fuente</div>
						<div class="panel-body">
							<pre class="brush: java; ruler: true;"><%=sourceCode%></pre>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>

<script type="text/javascript">
	SyntaxHighlighter.defaults.toolbar = false;
	SyntaxHighlighter.autoloader([ 'java', 'java', '<c:url value="/resources/js/syntaxhighlighter/shBrushJava.js"/>']);
	SyntaxHighlighter.all();
</script>