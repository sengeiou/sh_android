<%@page import="javax.servlet.RequestDispatcher"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<!DOCTYPE HTML>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/general.css"/>">
		<title>No se ha encontrado el recurso solicitado.</title>
	</head>
	<body>
		<h1>Recurso no encontrado</h1>
		<a href="<c:url value="/jsp/main.jsp"/>">
			<img src="<c:url value="/resources/img/home.png"/>" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<h2>El m&eacute;todo HTTP especificado no est&aacute; permitido para el recurso solicitado.</h2>
		<p><%=request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI)%></p>
	</body>
	<footer>
		<p></p>
	</footer>
</html>