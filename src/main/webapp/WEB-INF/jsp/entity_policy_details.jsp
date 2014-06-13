<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<c:choose>
		<c:when test="${entityPolicies.isVirtual()}"> 
		  	<c:set value="warning" var="panelClass"/>
		</c:when> 
		<c:otherwise>
			<c:set value="info" var="panelClass"/>
		</c:otherwise>
	</c:choose>

	<div class="panel panel-${panelClass}">

		<!-- Detalles de la entidad -->
		<c:choose>
			<c:when test="${entityPolicies.isVirtual()}">
				<div class="panel-heading"><h3>${entity} <sup>(virtual)</sup></h3></div>
			</c:when>
			<c:otherwise>
				<div class="panel-heading"><h3>${entity}</h3></div>
			</c:otherwise>
		</c:choose>

		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty entityPolicies}">
					La entidad <strong>${entity}</strong> no existe, o no es accesible.
				</c:when>
				<c:otherwise>
					<p>Detalle de las pol&iacute;ticas de acceso<p/>
					<!-- Operaciones permitidas. -->
					<div class="panel panel-info">
						<div class="panel-heading">Operaciones permitidas</div>
						<div class="panel-body">
							<c:choose>
								<c:when test="${empty entityPolicies.getAllowedOperations()}">
									ninguna.
								</c:when>
								<c:otherwise>
									<c:forEach items="${entityPolicies.getAllowedOperations()}" var="operation" varStatus="counter">
										<c:if test="${counter.count > 1}">
											<c:out value=", "/>
										</c:if>
										<c:out value="${operation.getOperationType()}"/>
									</c:forEach>
								</c:otherwise>
							</c:choose>
						</div>					
					</div>
					<!-- Dirección y  de los atributos. -->
					<div class="panel panel-info">
						<div class="panel-heading">Atributos</div>
						<div class="panel-body">
							<c:choose>
								<c:when test="${entityPolicies.getData() == null or empty entityPolicies.getData().getData()}">
									ninguno.
								</c:when>
								<c:otherwise>
									<table class="table">
										<thead><tr><th></th><th>Alias</th><th>Nombre en la fuente</th></tr></thead>
										<tbody>
											<c:forEach items="${entityPolicies.getData().getData()}" var="attribute">
												<c:choose>
													<c:when test="${attribute.getDirection() eq 'INPUT'}">
														<c:set value="red-input_64x64.png" var="direction"/>
													</c:when>
													<c:when test="${attribute.getDirection() eq 'OUTPUT'}">
														<c:set value="blue-output_64x64.png" var="direction"/>
													</c:when>
													<c:otherwise>
														<c:set value="input_output_arrows_64x64.png" var="direction"/>
													</c:otherwise>
												</c:choose>
												<tr>
													<td><img class="img-polaroid" width="16px" height="16px" src="<c:url value='/resources/img/${direction}'/>" alt=""/></td>
													<td>${attribute.getAlias()}</td>
													<td>${attribute.getName()}</td>
												</tr>												
											</c:forEach>
										</tbody>
									</table>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<!-- Keys disponibles. -->
					<div class="panel panel-info">
						<div class="panel-heading">Claves</div>
						<div class="panel-body">
							<c:if test="${entityPolicies.getOnlyByKey()}">
								<p><strong>Importante: </strong>
								esta entidad &uacute;nicamente es accesible mediate el uso de una de las claves especificadas a continuaci&oacute;n.	
							</c:if>
							<c:choose>
								<c:when test="${entityPolicies.getKeys() == null or empty entityPolicies.getKeys().getKeys()}">
									ninguna.
								</c:when>
								<c:otherwise>
									<ul class="list-group">
										<c:forEach items="${entityPolicies.getKeys().getKeys()}" var="key">
											<li class="list-group-item">
												<c:forEach items="${key.getKey()}" var="keyAttribute" varStatus="counter"><c:if test="${counter.count > 1}">, </c:if><c:out value="${keyAttribute.getAlias()}"/></c:forEach>
											</li>
										</c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<!-- Filtros disponibles. -->
					<div class="panel panel-info">
						<div class="panel-heading">Filtros</div>
						<div class="panel-body">
							<c:if test="${entityPolicies.getOnlySpecifiedFilters()}">
								<p><strong>Importante: </strong>
								esta entidad &uacute;nicamente es accesible mediate el uso de alguno de los filtros especificados a continuaci&oacute;n.	
							</c:if>
							<c:choose>
								<c:when test="${entityPolicies.getFilters() == null or empty entityPolicies.getFilters().getFilters()}">
									ninguno.
								</c:when>
								<c:otherwise>
									<ul class="list-group">
										<c:forEach items="${entityPolicies.getFilters().getFilters()}" var="filter">
											<li class="list-group-item">
												<c:forEach items="${filter.getFilter()}" var="filterAttribute" varStatus="counter"><c:if test="${counter.count > 1}">, </c:if><c:out value="${filterAttribute.getAlias()}"/></c:forEach>
											</li>
										</c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<!-- Ordenación por defecto. -->
					<div class="panel panel-info">
						<div class="panel-heading">Ordenaci&oacute;n</div>
						<div class="panel-body">
							<c:choose>
								<c:when test="${entityPolicies.getOrdination() == null or empty entityPolicies.getOrdination().getOrder()}">
									aleatoria.
								</c:when>
								<c:otherwise>
									<ul class="list-group">
										<c:forEach items="${entityPolicies.getOrdination().getOrder()}" var="orderAttribute" varStatus="counter"><c:if test="${counter.count > 1}">, </c:if><c:out value="${orderAttribute.getAlias()}"/> <c:out value="${orderAttribute.getOrder().getOrder()}"/></c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
					<!-- Puntos de inserción (Hooks). -->
					<div class="panel panel-info">
						<div class="panel-heading">Puntos de inserci&oacute;n (Hooks)</div>
						<div class="panel-body">
							<c:choose>
								<c:when test="${entityPolicies.getHooks() == null or empty entityPolicies.getHooks().keySet()}">
									ninguno.
								</c:when>
								<c:otherwise>
									<ul class="list-group">
										<c:forEach items="${entityPolicies.getHooks().keySet()}" var="hook" varStatus="counter"><c:if test="${counter.count > 1}">, </c:if><c:out value="${hook}"/></c:forEach>
									</ul>
								</c:otherwise>
							</c:choose>
						</div>
					</div>
				</c:otherwise>				
			</c:choose>
		</div>
	</div>
</div>

