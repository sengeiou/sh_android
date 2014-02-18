<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@page import="javax.servlet.RequestDispatcher"%>
<%@include file="/WEB-INF/jsp/includes/locations.jsp" %>

<!DOCTYPE HTML>
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<title>Error interno del servicio de datos.</title>
	</head>
	<body>
		<h1>Error interno <c:out value="${errorCode}"/></h1>
		<a href="<%=pagesURL%>/index.jsp">
			<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<h2>Se ha producido un error al realizar la llamada:</h2>
		<p><%=forwardRequestURI%></p>
		<p>
			<ul>
				<li><strong>errorCode:</strong> <c:out value="${errorCode}"/></li>
				<li><strong>message:</strong> <c:out value="${message}"/></li>
			</ul>
		</p>
	</body>
	<footer>
		<p></p>
	</footer>
</html>