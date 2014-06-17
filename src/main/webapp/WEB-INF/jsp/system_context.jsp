<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/tree.css"/>">

<!-- Panel de información del contexto de ejecución. -->
<div id="context">

	<!-- Ubicación y estructura del home directory -->
	<div class="panel panel-info">
		<div class="panel-heading">Ubicaci&oacute;n del directorio de inicio</div>
		<div class="panel-body">
			<p>Este es el directorio a partir del cual se obtiene toda la informaci&oacute;n de configuraci&oacute;n de esta instancia.</p>
			<p><strong>${contextHome}</strong></p>
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
			<li class="list-group-item"><Strong>Sistema operativo: </Strong>${contextSoName} ${contextSoArch} ${contextSoVersion}</li>
			<li class="list-group-item"><Strong>Usuario de ejecuci&oacute;n: </Strong>${contextSoUser}</li>
			<li class="list-group-item"><Strong>Interfaces de red: </Strong>
				<ul class="list-group">
					<c:forEach var="networkInterface" items="${contextNetworkIterfaces}">
						<li class="list-group-item">
						<c:forEach var="value" items="${networkInterface}" varStatus="counter">
							<c:choose>
								<c:when test="${counter.count == 1}">
									<strong>Descripción: </strong>${value}<br/>
								</c:when>
								<c:when test="${counter.count == 2}">
									<strong>Nombre: </strong>${value}<br/>
								</c:when>
								<c:when test="${counter.count == 3}">
									<strong>Direcciones asociadas: </strong>${value}<br/>
								</c:when>
								<c:otherwise>
									<c:if test="${value}">, {value}</c:if>
								</c:otherwise>
							</c:choose>
						</c:forEach>
						</li>
					</c:forEach>
				</ul>
			 </li>
			<li class="list-group-item"><Strong>Ip externa: </Strong>${contextNetworkExternalIP}</li>
			<li class="list-group-item"><Strong>Java: </Strong>${contextJavaVendor} ${contextJavaVersion}</li>
			<li class="list-group-item"><Strong>Web Container: </Strong>${contextContainerInfo}</li>
		</ul>
	</div>

	<!-- Classpath y dependencias -->
	<div class="panel panel-info">
		<div class="panel-heading">Class Path</div>
		<div class="panel-body">
			<p>Class path de ejecuci&oacute;n usado para la compilaci&oacute;n carga y ejecuci&oacute;n de los puntos de inserci&oacute;n (hooks).</p>
		</div>
		
		<ul class="list-group">
			<li class="list-group-item"><Strong>Rutas de clases</Strong>
			<br/>
				<c:forEach var="classpath" items="${contextJavaClasspathClasspaths}">
					<c:if test="${not empty classpath}"><br/>${classpath}</c:if>
				</c:forEach>
			</li>
			
			<li class="list-group-item"><Strong>Dependencias</Strong>
			<br/>
				<c:forEach var="dependency" items="${contextJavaClasspathDependencies}">
					<c:if test="${not empty dependency}">
						
						<c:set var="dependencySlices" value="${fn:split(dependency, '/')}"/>
						<c:set var="numSlices" value="${fn:length(dependencySlices)}"/>
						
						<br/><font color='blue'><c:out value="${dependencySlices[numSlices-1]}"></c:out></font> - ${dependency}
					</c:if>
				</c:forEach>
			</li>
		</ul>
	</div>

</div>