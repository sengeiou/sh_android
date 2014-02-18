<%@page import="java.util.List"%>

<%!String hostPortURL; %>
<%!String baseURL; %>
<%!String pagesURL; %>
<%!String cssURL; %>
<%!String imagesURL; %>
<%!String forwardRequestURI;%>
<%
	hostPortURL = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf(request.getContextPath()));
	forwardRequestURI = hostPortURL + (String) request.getAttribute(RequestDispatcher.FORWARD_REQUEST_URI);
	baseURL = hostPortURL + request.getContextPath();
	pagesURL = baseURL + "/jsp";
	imagesURL = baseURL + "/resources/img";
	cssURL = baseURL + "/resources/css";
%>
