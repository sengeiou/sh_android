<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="customFunctions.tld" prefix="cf"%>


<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick='showAvailableCacheManagers();' type='button' class='btn btn-default btn-sm pull-right'>
   			<span class='glyphicon glyphicon-th-list'></span>
   		</button>
		<div class="panel-heading"><h3>${cacheManager}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty cacheManagerConfiguration}">
					El gestor de cach&eacute; <strong>${cacheManager}</strong> no existe.
				</c:when>
				<c:otherwise>
					<p>Detalle de la configuraci&oacute;n<p/>
					
					<!-- Límites de almacenamiento del gestor de caché. -->
					<div class="panel panel-info">
						<div class="panel-heading">L&iacute;mites de alacenamiento</div>
						<br/>
						<div class="panel-body">
							<ul class="list-group">
								<li class="list-group-item"><strong>Memoria:</strong> ${cf:fromBytesToString(cacheManagerConfiguration.getMaxBytesLocalHeap())}</li>
								<li class="list-group-item"><strong>Disco:</strong> ${cf:fromBytesToString(cacheManagerConfiguration.getMaxBytesLocalDisk())}</li>
							</ul>
						</div>
					</div>
					
					<!-- Ubicación de los ficheros de caché en disco. -->
					<div class="panel panel-info">
						<div class="panel-heading">Ubicaci&oacute;n de los ficheros de cach&eacute; en disco</div>
						<div class="panel-body">
							<p>${cacheManagerConfiguration.getDiskStore().getPath()}</p>
						</div>
					</div>
					
					<!-- Entidades de caché -->
					<c:choose>
						<c:when test="${empty cacheManagerConfiguration.getEntitiesCacheConfigurations()}">
							<div class="panel-body">
								<p>No ning&uacute;n gestor de cach&eacute; configurado.</p>
							</div>
						</c:when>
						<c:otherwise>
							<div class="panel-body">
								<p>Conjunto de cach&eacute;s de entidades.</p>
							</div>
							<table class="table">
								<thead><tr><th></th><th>Memoria</th><th>%</th><th>Disco</th><th>%</th></tr></thead>
								<tbody>
									<c:forEach var="cacheConfiguration" items="${cacheManagerConfiguration.getEntitiesCacheConfigurations()}">
										<tr>
											<c:set var="maxBytesLocalHeap" value="${cacheConfiguration.getMaxBytesLocalHeap() / cacheManagerConfiguration.getMaxBytesLocalHeap()}"/>
											<c:set var="maxBytesLocalHeap" value="${maxBytesLocalHeap * 100}"/>
											<fmt:formatNumber var="maxBytesLocalHeap" value="${maxBytesLocalHeap}"  minFractionDigits="2" maxFractionDigits="2" />

											<c:set var="maxBytesLocalDisk" value="${cacheConfiguration.getMaxBytesLocalDisk() / cacheManagerConfiguration.getMaxBytesLocalDisk()}"/>
											<c:set var="maxBytesLocalDisk" value="${maxBytesLocalDisk * 100}"/>
											<fmt:formatNumber var="maxBytesLocalDisk" value="${maxBytesLocalDisk}"  minFractionDigits="2" maxFractionDigits="2" />

											<td><a href="#" onclick="sendGetRequest('cache/cacheConfiguration?cacheManager=${cacheManagerConfiguration.getName()}&entity=${cacheConfiguration.getAlias()}');">${cacheConfiguration.getAlias()}</a><br/></td>
											<td>${cf:fromBytesToString(cacheConfiguration.getMaxBytesLocalHeap())}</td>
											<td>${maxBytesLocalHeap}</td>
											<td>${cf:fromBytesToString(cacheConfiguration.getMaxBytesLocalDisk())}</td>
											<td>${maxBytesLocalDisk}</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
