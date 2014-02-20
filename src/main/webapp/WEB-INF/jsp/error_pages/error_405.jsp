<%@page import="javax.servlet.RequestDispatcher"%>
<%@include file="/WEB-INF/jsp/includes/locations.jsp" %>

<!DOCTYPE HTML>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<title>No se ha encontrado el recurso solicitado.</title>
	</head>
	<body>
		<h1>Recurso no encontrado</h1>
		<a href="<%=pagesURL%>/index.jsp">
			<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<h2>El m&eacute;todo HTTP especificado no est&aacute; permitido para el recurso solicitado.</h2>
		<p><%=forwardRequestURI%></p>
	</body>
	<footer>
		<p></p>
	</footer>
</html>