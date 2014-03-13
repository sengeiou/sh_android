<%@page import="java.util.List"%>

<%!String hostPortURL; %>
<%!String baseURL; %>
<%!String servicesURL; %>
<%!String pagesURL; %>
<%!String cssURL; %>
<%!String jsURL; %>
<%!String imagesURL; %>
<%!String forwardRequestURI;%>
<%
	hostPortURL = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf(request.getContextPath()));
	forwardRequestURI = hostPortURL + (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
	baseURL = hostPortURL + request.getContextPath();
	servicesURL = baseURL + "/rest";
	pagesURL = baseURL + "/jsp";
	imagesURL = baseURL + "/resources/img";
	cssURL = baseURL + "/resources/css";
	jsURL = baseURL + "/resources/js";
%>
