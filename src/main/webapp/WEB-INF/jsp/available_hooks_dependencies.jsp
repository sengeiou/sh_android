<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/general.css"/>"/>


<!-- Panel de información de las dependencias disponibles para los hooks. -->
<div id="availableHooksDependencies">

	<!-- Dependencias disponibles para los Hooks. -->
	<div class="panel panel-info">
		<div class="panel-heading">Dependencias disponibles para hooks</div>
		<!-- Lista de dependencias. -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty hooksDependencies}">
					<p>No hay dependencias disponibles.</p>
				</c:when>
				<c:otherwise>
					<c:forEach var="hooksDependency" items="${hooksDependencies}">
						<c:if test="${not empty hooksDependency}">
							
							<c:set var="hooksDependencySlices" value="${fn:split(hooksDependency, '/')}"/>
							<c:set var="numSlices" value="${fn:length(hooksDependencySlices)}"/>
							
							<br/><font color="blue"><c:out value="${hooksDependencySlices[numSlices-1]}"></c:out></font> - ${hooksDependency}
						</c:if>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</div>
	</div>
</div>
