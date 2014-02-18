<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="includes/locations.jsp" %>

<html>
<head>
	<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
	<link rel="stylesheet" type="text/css" href="<%=cssURL%>/buttons.css">
    <title>Resultado de la carga de ficheros de pol&iacute;ticas de acceso</title>
</head>
<body>
    <h1>Resultado de la carga</h1>
	<a href="<%=pagesURL%>/index.jsp">
		<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
	</a>
	<h2>Carga de ficheros de pol&iacute;ticas de acceso</h2>
	
	<form:form method="get" action="accessPolicyUpload.show">
	
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
		    	<p><strong>No</strong> se ha cargado, ning&uacute; fichero.</p>
		    </c:otherwise>
	    </c:choose>
	    
		<c:if test="${filesKO.size() > 0}">
		    <p><strong>No</strong> ha sido posible cargar, los siguientes archivos.</p>
		    <ul>
		        <c:forEach items="${filesKO}" var="file" varStatus="counter">
	            <li>
	            	<strong>Fichero:</strong> <c:out value="${file}"/><br/>
	            	<strong>Causa:</strong> <c:out value="${filesErrors[counter.count-1]}"/>
	            </li>
	        </c:forEach>
		    </ul>
		</c:if>
		<br/>
		<div class="center">
			<button class="blue-pill" type="submit">Cargar m&aacute;s ficheros</button>
		</div>
	</form:form>
</body>
</html>
