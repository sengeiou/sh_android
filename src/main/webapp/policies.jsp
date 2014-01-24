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
			output.append("Listado de entidades públicas:")
					.append('\n');

			for (String currentEntity : accessPolicyService
					.getPublicEntities()) {
				output.append(currentEntity).append('\n');
			}

		} else {
			EntityAccessPolicy entityAccessPolicy = accessPolicyService
					.getPublicEntityPolicy(entity);

			if (entityAccessPolicy != null) {
				output.append("Detalle de las políticas de acceso\n");
				output.append(entityAccessPolicy.getDetails());
			} else {
				output.append("La entidad ").append(entity)
						.append(" no existe, o no es accesible.");
			}
		}

	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>
<html>
<body><%=bodyContent%>
</body>
</html>