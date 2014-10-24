<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<div class="panel panel-danger">
	<div class="panel-heading">
		<h2 class="panel-title">Error interno <c:out value="${errorCode}"/></h2>
	</div>
	<div class="panel-body">
		<h3>Se ha producido un error al realizar la llamada:</h3>
		<p><%=request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)%></p>
		<p>
			<ul>
				<li><strong>errorCode:</strong> <c:out value="${errorCode}"/></li>
				<li><strong>message:</strong> <c:out value="${message}"/></li>
				<li><strong>explanation:</strong> ${explanation}</li>
			</ul>
		</p>
	</div>
</div>
