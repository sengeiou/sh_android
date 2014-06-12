<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="java.io.File"%>
<%@page import="com.fav24.dataservices.util.JDBCUtils"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<%!String contextHome;%>
<%!String contextSoName;%>
<%!String contextSoArch;%>
<%!String contextSoVersion;%>
<%!String contextSoUser;%>
<%!String contextContainerInfo;%>
<%!List<String[]> contextNetworkInternalIPs;%>
<%!String HTMLInternalIPs;%>
<%!String contextNetworkExternalIP;%>
<%!String contextJavaVendor;%>
<%!String contextJavaVersion;%>
<%!List<String> contextJavaClassPaths;%>
<%!List<String> contextJavaDependencies;%>
<%!String HTMLJavaClassPaths;%>
<%!String HTMLJavaDependencies;%>

<%
	contextHome = (String)request.getAttribute("context.home");
	contextSoName = (String)request.getAttribute("context.so.name");
	contextSoArch = (String)request.getAttribute("context.so.arch");
	contextSoVersion = (String)request.getAttribute("context.so.version");
	contextSoUser = (String)request.getAttribute("context.so.user");
	contextContainerInfo = (String)request.getAttribute("context.container.info");
	contextNetworkInternalIPs = (List<String[]>)request.getAttribute("context.network.internal-ip");
	contextNetworkExternalIP = (String)request.getAttribute("context.network.external-ip");
	contextJavaVendor = (String)request.getAttribute("context.java.vendor");
	contextJavaVersion = (String)request.getAttribute("context.java.version");
	contextJavaClassPaths = (List<String>)request.getAttribute("context.java.classpath.classpaths");
	contextJavaDependencies = (List<String>)request.getAttribute("context.java.classpath.dependencies");

	StringBuilder output = new StringBuilder();
	
	output.append("<ul class='list-group'>");
	for(String contextNetworkInternalIP[] : contextNetworkInternalIPs) {
		output.append("<li class='list-group-item'>");
		output.append("<strong>Descripción: </strong>").append(contextNetworkInternalIP[0]);
		output.append("<br/><strong>Nombre: </strong>").append(contextNetworkInternalIP[1]);
		output.append("<br/><strong>Direcciones asociadas: </strong>");
		
		output.append(contextNetworkInternalIP[2]);
		for(int i = 3; i < contextNetworkInternalIP.length;i++) {
			if (contextNetworkInternalIP[i] != null) {
				output.append(", ").append(contextNetworkInternalIP[i]);
			}
		}
		output.append("</li>");
	}
	output.append("</ul>");
	
	HTMLInternalIPs = output.toString();
	
	output = null;
	
	for (String javaClassPath : contextJavaClassPaths) {
		
		if (output == null) {
			output = new StringBuilder();
		}
		else {
			output.append("<br/>");
		}

		output.append(javaClassPath);
	}
	
	HTMLJavaClassPaths = output.toString();
	
	output = null;
	
	for (String javaDependency : contextJavaDependencies) {
		
		if (output == null) {
			output = new StringBuilder();
		}
		else {
			output.append("<br/>");
		}

		String moduleName = javaDependency.substring(javaDependency.lastIndexOf(File.separator)+1);
		
		output.append("<font color='blue'>").append(moduleName).append("</font>").append(" - ").append(javaDependency);
	}
	
	HTMLJavaDependencies = output.toString();
%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/tree.css"/>">

<!-- Panel de información del contexto de ejecución. -->
<div id="context">

	<!-- Ubicación y estructura del home directory -->
	<div class="panel panel-info">
		<div class="panel-heading">Ubicaci&oacute;n del directorio de inicio</div>
		<div class="panel-body">
			<p>Este es el directorio a partir del cual se obtiene toda la informaci&oacute;n de configuraci&oacute;n de esta instancia.</p>
			<p><strong><%=contextHome%></strong></p>
		</div>
		
		<!-- Árbol de directorios y ficheros -->
		<!-- 
		<div class="tree">
		    <ul>
		        <li>
		            <span><i class="glyphicon glyphicon-folder-open"></i> Parent</span> <a href="">Goes somewhere</a>
		            <ul>
		                <li>
		                	<span><i class="glyphicon glyphicon-minus-sign"></i> Child</span> <a href="">Goes somewhere</a>
		                    <ul>
		                        <li>
			                        <span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
		                        </li>
		                    </ul>
		                </li>
		                <li>
		                	<span><i class="glyphicon glyphicon-minus-sign"></i> Child</span> <a href="">Goes somewhere</a>
		                    <ul>
		                        <li>
			                        <span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
		                        </li>
		                        <li>
		                        	<span><i class="glyphicon glyphicon-minus-sign"></i> Grand Child</span> <a href="">Goes somewhere</a>
		                            <ul>
		                                <li>
			                                <span><i class="glyphicon glyphicon-minus-sign"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
				                            <ul>
				                                <li>
					                                <span><i class="glyphicon glyphicon-leaf"></i> Great great Grand Child</span> <a href="">Goes somewhere</a>
				                                </li>
				                                <li>
					                                <span><i class="glyphicon glyphicon-leaf"></i> Great great Grand Child</span> <a href="">Goes somewhere</a>
				                                </li>
				                             </ul>
		                                </li>
		                                <li>
			                                <span><i class="glyphicon glyphicon-leaf"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
		                                </li>
		                                <li>
			                                <span><i class="glyphicon glyphicon-leaf"></i> Great Grand Child</span> <a href="">Goes somewhere</a>
		                                </li>
		                            </ul>
		                        </li>
		                        <li>
			                        <span><i class="glyphicon glyphicon-leaf"></i> Grand Child</span> <a href="">Goes somewhere</a>
		                        </li>
		                    </ul>
		                </li>
		            </ul>
		        </li>
			        <li>
			            <span><i class="glyphicon glyphicon-folder-open"></i> Parent2</span> <a href="">Goes somewhere</a>
			            <ul>
			                <li>
			                	<span><i class="glyphicon glyphicon-leaf"></i> Child</span> <a href="">Goes somewhere</a>
					        </li>
					    </ul>
			        </li>
		    </ul>
		</div>
	 -->
	</div>
	
	<!-- Componentes -->
	<div class="panel panel-info">
		<div class="panel-heading">Componentes</div>
		<div class="panel-body">
			<p>Informaci&oacute;n de la versi&oacute;n de los componentes implicados en esta instancia.</p>
		</div>

		<!-- Lista componentes y versiones -->
		<ul class="list-group">
			<li class="list-group-item"><Strong>Sistema operativo: </Strong><%=contextSoName%> <%=contextSoArch%> <%=contextSoVersion%></li>
			<li class="list-group-item"><Strong>Usuario de ejecuci&oacute;n: </Strong><%=contextSoUser%></li>
			<li class="list-group-item"><Strong>Interfaces de red: </Strong> <%=HTMLInternalIPs%></li>
			<li class="list-group-item"><Strong>Ip externa: </Strong><%=contextNetworkExternalIP%></li>
			<li class="list-group-item"><Strong>Java: </Strong><%=contextJavaVendor%> <%=contextJavaVersion%></li>
			<li class="list-group-item"><Strong>Web Container: </Strong><%=contextContainerInfo%></li>
		</ul>
	</div>

	<!-- Classpath -->
	<div class="panel panel-info">
		<div class="panel-heading">Class Path</div>
		<div class="panel-body">
			<p>Class path de ejecuci&oacute;n usado para la compilaci&oacute;n carga y ejecuci&oacute;n de los puntos de inserci&oacute;n (hooks).</p>
		</div>
		
		<ul class="list-group">
			<li class="list-group-item"><Strong>Rutas de clases</Strong>
			<br/>
			<%=HTMLJavaClassPaths%>
			</li>
			
			<li class="list-group-item"><Strong>Dependencias</Strong>
			<br/>
			<%=HTMLJavaDependencies%>
			</li>
		</ul>
	</div>

</div>