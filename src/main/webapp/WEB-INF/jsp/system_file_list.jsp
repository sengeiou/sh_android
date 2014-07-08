<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="customFunctions.tld" prefix="cf"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/tree.css"/>">

<!-- Panel de información del sistema de archivos. -->
<div id="context">

	<!-- Árbol de directorios y ficheros -->
	<div class="panel panel-info">
		<div class="panel-heading">&Aacute;rbol de directorios y ficheros
		</div>
		<div class="panel-body">
			<p>Directorio actual:</p>
			<p>
				<button onClick="showAvailableHooks();" type="button" class="btn btn-default btn-sm pull-left">
		   			<span class="glyphicon glyphicon-arrow-left"></span>
		   		</button>
		   		
				<button onClick="sendGetRequest('system/fileInformationList?path=${basePath}&pattern=&directoriesOnly=false&filesOnly=false');" type="button" class="btn btn-default btn-sm pull-left">
		   			<span class="glyphicon glyphicon-refresh"></span>
					<strong>${basePath}</strong>
		   		</button>
			</p>
		</div>

		<table class="table table-condensed">
			<thead>
			<tr>
				<th></th>
				<th>Nombre</th>
				<th align="center">Permisos</th>
				<th align="right">Tamaño</th>
				<th>Creaci&oacute;n</th>
				<th>Modificac&oacute;n</th>
				<th></th>
			</tr>
			</thead>
			<tbody>
				<jsp:useBean id="dateValue" class="java.util.Date" />
				<c:forEach var="fileInformation" items="${fileInformationList}">
					<tr>
						<c:choose>
							<c:when test="${fileInformation.isDirectory()}">
								<td><span><i class="glyphicon glyphicon-folder-open"></i></span></td>
							</c:when>
							<c:otherwise>
								<td><span><i class="glyphicon glyphicon-file"></i></span></td>
							</c:otherwise>
						</c:choose>
						<td>${fileInformation.getName()}</td>
						<td align="center" style="font-family:'Lucida Console', Monaco, monospace">${fileInformation.getPermissions()}</td>
						<td align="right">${cf:fromBytesToString(fileInformation.getSize())}</td>
						<jsp:setProperty name="dateValue" property="time" value="${fileInformation.getCreationTime()}" />
						<td><fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<jsp:setProperty name="dateValue" property="time" value="${fileInformation.getLastModifiedTime()}" />
						<td><fmt:formatDate value="${dateValue}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td>
							<button onClick='showAvailableHooks();' type='button' class='btn-default glyphicon glyphicon-download'></button>
				   		</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>