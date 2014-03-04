<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="includes/locations.jsp" %>


<div class="panel panel-success">
	<div class="panel-heading">
		<h2 class="panel-title">Resultado de la carga</h2>
	</div>
	<div class="panel-body">
		<h3>Carga de ficheros de pol&iacute;ticas de acceso</h3>
		<c:choose>
			<c:when test="${filesOK.size() > 0}">
		    <p>Los siguientes archivos han sido cargados satisfactoriamente.</p>
		    <ul>
		        <c:forEach items="${filesOK}" var="file">
		            <li><c:out value="${file}"/></li>
		        </c:forEach>
		    </ul>
		    </c:when>
		    <c:otherwise>
		    	<p><strong>No</strong> se ha cargado, ning&uacute;n fichero.</p>
		    </c:otherwise>
	    </c:choose>
	</div>
</div>

<c:if test="${filesKO.size() > 0}">
	<div class="panel panel-danger">
		<div class="panel-heading">
			<h2 class="panel-title">Cargas err&oacute;neas</h2>
		</div>
		<div class="panel-body">
			<h3><strong>No</strong> ha sido posible cargar, los siguientes archivos.</h3>
			<ul>
				<c:forEach items="${filesKO}" var="file" varStatus="counter">
					<li>
						<strong>Fichero:</strong> <c:out value="${file}"/><br/>
						<strong>Causa:</strong> <c:out value="${filesErrors[counter.count-1]}"/>
					</li>
				</c:forEach>
			</ul>
		</div>
	</div>
</c:if>


