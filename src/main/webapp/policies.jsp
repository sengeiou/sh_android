<%@ page import="org.springframework.web.context.support.*"%>
<%@ page import="org.springframework.web.context.*"%>
<%@ page import="com.fav24.dataservices.security.*"%>
<%@ page import="com.fav24.dataservices.service.security.*"%>

<%!String bodyContent;%>
<%
	WebApplicationContext context = WebApplicationContextUtils
			.getWebApplicationContext(this.getServletContext());

	RetrieveAccessPolicyService accessPolicyService = context
			.getBean(RetrieveAccessPolicyService.class);

	StringBuilder output = new StringBuilder();

	try {
		String entity = request.getParameter("Entity");

		if ("*".equals(entity)) {
			output.append("<p><b>Entidades p&uacute;blicas:</b>").append("<br/>");

			for (String currentEntity : accessPolicyService
					.getPublicEntities()) {
				output.append(currentEntity).append("<br/>");
			}
			
			output.append("</p>");
		} else {
			EntityAccessPolicy entityAccessPolicy = accessPolicyService
					.getPublicEntityPolicy(entity);

			output.append("<p>");
			if (entityAccessPolicy != null) {
				output.append("Detalle de las pol&iacute;ticas de acceso<br/>");
				output.append(entityAccessPolicy.getDetails(true));
			} else {
				output.append("La entidad <b>").append(entity)
						.append("</b> no existe, o no es accesible.");
			}
			output.append("</p>");
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<!DOCTYPE HTML>
<html>
	<head>
		<title>Pol&iacute;ticas de acceso</title>
	</head>
	<body>
		<%=bodyContent%>
	</body>
	<footer>
	  <p><b><u>Servicios para el mantenimiento de las pol&iacute;ticas:</u></b></p>
	  <p>End-point del servicio de carga: <b>http://host:port/data-services/rest/accesspolicy/load</b></p>
	  <p>Ejemplo: </p>
	  <p>{<br/>
		<span style='color:red'>&quot;status&quot;</span>: {<br/>
				<span style='color:red'>&quot;code&quot;</span>: &quot;OK&quot;,<br/>
				<span style='color:red'>&quot;message&quot;</span>:&quot;&quot;<br/>
				},<br/>
		<span style='color:red'>&quot;req&quot;</span>:[<span style='color:green'>0, 0, 0, 4003000, 100000000000</span>],<br/>
		<span style='color:red'>&quot;policyFiles&quot;</span>:[<br/> 							
	  <span style='color:green'>
	  	&quot;file:///Users/jmvera/development/workspaces/backend/data-services/src/test/resources/test/com/fav24/dataservices/xml/gm-entidades-base.xml&quot;
	  </span><br/>
	  ]<br/>
	  }</p>
	  <p>End-point del servicio de consulta de pol&iacute;ticas: <b>http://host:port/data-services/rest/accesspolicy/retrieve</b></p>
	  <p>Ejemplo: </p>
	  <p>{<br/>
		<span style='color:red'>&quot;status&quot;</span>: {<br/>
				<span style='color:red'>&quot;code&quot;</span>: &quot;OK&quot;,<br/>
				<span style='color:red'>&quot;message&quot;</span>:&quot;&quot;<br/>
				},<br/>
		<span style='color:red'>&quot;req&quot;</span>:[<span style='color:green'>0, 0, 0, 4003000, 100000000000</span>],<br/>
		<span style='color:red'>&quot;policies&quot;</span>:[]<br/> 							
	  }</p>
	</footer>
</html>