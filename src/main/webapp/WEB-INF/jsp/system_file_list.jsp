<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@taglib uri="customFunctions.tld" prefix="cf"%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/custom/tree.css"/>"/>

<!-- Panel de información del sistema de archivos. -->
<div id="context">

	<!-- Árbol de directorios y ficheros -->
	<div class="panel panel-info">
		<div class="panel-heading">&Aacute;rbol de directorios y ficheros</div>
		<div class="panel-body">
			<div>
				<button onClick="sendGetRequest('system/fileInformationList?path=${basePath}&parent=true&pattern=&directoriesOnly=false&filesOnly=false');" type="button" class="btn btn-default btn-sm pull-left">
		   			<span class="glyphicon glyphicon-arrow-left"></span>
		   		</button>
		   		
		   		<button onClick="sendGetRequest('system/fileInformationList?path=${basePath}&parent=false&pattern=&directoriesOnly=false&filesOnly=false');" type="button" class="btn btn-default btn-sm pull-right">
		   			<span class="glyphicon glyphicon-refresh"></span>
		   		</button>

		   		<h5 class="text-center text-info">${basePath}</h5>
			</div>
		</div>

		<table class="table table-condensed table-hover">
			<thead>
			<tr>
				<th></th>
				<th class="text-left">Nombre</th>
				<th class="text-center">Grupo:Propietario</th>
				<th class="text-center">Permisos</th>
				<th class="text-right">Tamaño</th>
				<th class="text-center">Creaci&oacute;n</th>
				<th class="text-center">Modificac&oacute;n</th>
				<th></th>
			</tr>
			</thead>
			<tbody>
				<jsp:useBean id="creationTime" class="java.util.Date" />
				<jsp:useBean id="lastModifiedTime" class="java.util.Date" />
				<c:forEach var="fileInformation" items="${fileInformationList}">
					
					<jsp:setProperty name="creationTime" property="time" value="${fileInformation.getCreationTime()}" />
					<jsp:setProperty name="lastModifiedTime" property="time" value="${fileInformation.getLastModifiedTime()}" />
					
					<c:if test="${fileInformation.isDirectory()}">
						<c:set var="newPath" value="${basePath}${cf:getFileSeparator()}${fileInformation.getName()}"></c:set> 
						<c:set var="navigateForward" value="sendGetRequest('system/fileInformationList?path=${newPath}&parent=false&pattern=&directoriesOnly=false&filesOnly=false');"></c:set> 
					</c:if>
					
					<tr ondblclick="${navigateForward}">
						<c:choose>
							<c:when test="${fileInformation.isDirectory()}">
								<td onClick="${navigateForward}" class="text-center"><span class="glyphicon glyphicon-folder-open"></span></td>
							</c:when>
							<c:otherwise>
								<td class="text-center"><span class="glyphicon glyphicon-file"></span></td>
							</c:otherwise>
						</c:choose>
						<td class="text-left">${fileInformation.getName()}</td>
						<td class="text-center">${fileInformation.getGroup()}:${fileInformation.getOwner()}</td>
						<td class="text-center" style="font-family:'Lucida Console', Monaco, monospace;">${fileInformation.getPermissions()}</td>
						<td class="text-right">${cf:fromBytesToString(fileInformation.getSize())}</td>
						<td class="text-center"><fmt:formatDate value="${creationTime}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="text-center"><fmt:formatDate value="${lastModifiedTime}" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="text-center">
							<a id="${fileInformation.getName()}" href="" class="glyphicon glyphicon-download"></a>
							<script type="text/javascript">
									var file = App.pagesURL + "/system/downloadFile?file=${basePath}${cf:getFileSeparator()}${fileInformation.getName()}&compress=false";
									var link = document.getElementById("${fileInformation.getName()}");
									link.setAttribute("href", file);
							</script>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
