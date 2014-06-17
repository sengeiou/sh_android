<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


<!-- Panel de información de las entidades publicadas. -->
<div id="availableEntities">

	<!-- Entidades disponibles -->
	<div class="panel panel-info">
		<div class="panel-heading">Entidades disponibles</div>
		<div class="panel-body">
		<!-- Lista de entidades -->
		<c:choose>
			<c:when test="${entities == null or empty entities}">
				No hay entidades disponibles.
			</c:when>
			<c:otherwise>
				<p>Estas son las entidades de datos que esta instancia est&aacute; ofreciendo en estos momentos.</p>
				<br/>
				<div class="list-group">
					<c:forEach items="${entities}" var="entity">
						<c:choose>
							<c:when test="${fn:contains(virtualEntities, entity)}">
								<c:set value="list-group-item list-group-item-warning" var="entityClass"/>
							</c:when>
							<c:otherwise>
								<c:set value="list-group-item" var="entityClass"/>
							</c:otherwise>
						</c:choose>					
						<a class="${entityClass}" href="#" onclick="sendGetRequest('accesspolicy/entityPolicies?entity=${entity}')">${entity}</a>
					</c:forEach>
				</div>
			</c:otherwise>
		</c:choose>
		</div>
	</div>
</div>
