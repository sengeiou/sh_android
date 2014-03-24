<%@page import="java.util.Map"%>
<%@page import="com.fav24.dataservices.util.JDBCUtils"%>

<%!Map<String, String> dataSourceAttributes;%>
<%!Map<String, String> statsDataSourceAttributes;%>
<%
	dataSourceAttributes = (Map<String, String>)request.getAttribute("dataSource");
	statsDataSourceAttributes = (Map<String, String>)request.getAttribute("statsDataSource");
%>

<!-- Panel de información de las fuentes de datos implicadas. -->
<div id="dataSources">

	<!-- Fuente de datos que se sirve -->
	<div class="panel panel-info">
		<div class="panel-heading">Fuente de datos</div>
		<div class="panel-body">
			<p>Esta es la fuentes de datos que esta instancia est&aacute; publicando en estos momentos.</p>
		</div>
		
		<!-- Atributos de la instancia -->
		<ul class="list-group">
			<li class="list-group-item"><Strong>Servidor: </Strong><%=dataSourceAttributes.get(JDBCUtils.HOST)%></li>
			<li class="list-group-item"><Strong>Puerto: </Strong><%=dataSourceAttributes.get(JDBCUtils.PORT) == null?"default":dataSourceAttributes.get(JDBCUtils.PORT)%></li>
			<li class="list-group-item"><Strong>Versi&oacute;n: </Strong><%=dataSourceAttributes.get(JDBCUtils.DATABASE_VERSION)%></li>
			<li class="list-group-item"><Strong>Ruta: </Strong><%=dataSourceAttributes.get(JDBCUtils.PATH)%></li>
			<li class="list-group-item"><Strong>Usuario: </Strong><%=dataSourceAttributes.get(JDBCUtils.USER)%></li>
			<li class="list-group-item"><Strong>Cadena de conexi&oacute;n: </Strong><%=dataSourceAttributes.get(JDBCUtils.URL)%></li>
		</ul>
	</div>

	<!-- Fuente de datos estadísticos -->
	<div class="panel panel-info">
		<div class="panel-heading">Ubicaci&oacute;n de la informaci&oacute;n de uso</div>
		<div class="panel-body">
			<p>Esta es la fuente de datos en que se almacena la informaci&oacute;n de uso de esta instancia.</p>
		</div>
		
		<!-- Atributos de la instancia -->
		<ul class="list-group">
			<li class="list-group-item"><Strong>Servidor: </Strong><%=dataSourceAttributes.get(JDBCUtils.HOST)%></li>
			<li class="list-group-item"><Strong>Puerto: </Strong><%=dataSourceAttributes.get(JDBCUtils.PORT) == null?"default":dataSourceAttributes.get(JDBCUtils.PORT)%></li>
			<li class="list-group-item"><Strong>Ruta: </Strong><%=dataSourceAttributes.get(JDBCUtils.PATH)%></li>
			<li class="list-group-item"><Strong>Usuario: </Strong><%=dataSourceAttributes.get(JDBCUtils.USER)%></li>
			<li class="list-group-item"><Strong>Cadena de conexi&oacute;n: </Strong><%=dataSourceAttributes.get(JDBCUtils.URL)%></li>
		</ul>
	</div>
</div>