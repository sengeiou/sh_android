<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="customFunctions.tld" prefix="cf"%>

<!-- Panel de información de las entidades publicadas. -->
<div id="availableEntities">

	<!-- Entidades disponibles -->
	<div class="panel panel-info">
		<div class="panel-heading">Gestores de cach&eacute; disponibles</div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty cacheManagers}">
					<p>No ning&uacute;n gestor de cach&eacute; configurado.</p>
				</c:when>
				<c:otherwise>
					<p>Conjunto de gestores de cach&eacute; configurados en esta instancia de servicio de datos.</p>
					<table class="table">
						<thead><tr><th></th><th>Memoria</th><th>Disco</th><th>Ubicaci&oacute;n de los ficheros</th></tr></thead>
						<tbody>
							<c:forEach var="cacheManager" items="${cacheManagers}">
								<tr>
									<td><a href="#" onclick="sendGetRequest('cache/cacheManagerConfiguration?cacheManager=${cacheManager.getName()}');">${cacheManager.getName()}</a></td>
									<td>${cf:fromBytesToString(cacheManager.getMaxBytesLocalHeap())}</td>
									<td>${cf:fromBytesToString(cacheManager.getMaxBytesLocalDisk())}</td>
									<td>${cacheManager.getDiskStore().getPath()}</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
