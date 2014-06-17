<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!-- Panel de información de los hooks cargados. -->
<div id="availableHooks">

	<!-- Hooks cargados -->
	<div class="panel panel-info">
		<div class="panel-heading">Hooks disponibles</div>
		<!-- Lista de hooks -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty hooks}">
					<p>No hay hooks disponibles.</p>
				</c:when>
				<c:otherwise>
					<p>Estos son los puntos de inserci&oacute;n (hooks) disponibles en estos momentos.</p>
					<br/>
					<div class="list-group">
						<c:forEach var="hook" items="${hooks}">
							<a class="list-group-item" href="#" onclick="sendGetRequest('hook/hookDetails?hook=${hook.getKey()}');">${hook.getKey()}</a>
						</c:forEach>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
