<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@include file="includes/locations.jsp" %>

<html>
	<head>
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/basics.css">
		<link rel="stylesheet" type="text/css" href="<%=cssURL%>/buttons.css">
	    <title>Carga de ficheros de pol&iacute;ticas de acceso</title>
	    
		<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
		<script>
		$(document).ready(function() {
		    // Añade una nueva fila para cargar otro fichero.
		    $('#addFile').click(function() {
		        var fileIndex = $('#fileTable tr').children().length - 1;
		        $('#fileTable').append(
		                '<tr style="width: 100%;"><td>'+
		                '   <input style="width: 100%;" type="file" name="files['+ fileIndex +']" />'+
		                '</td></tr>');
		    });
		     
		});
		</script>
	</head>
	<body>
		<h1>Carga de pol&iacute;ticas de acceso</h1>
		<a href="<%=pagesURL%>/index.jsp">
			<img src="<%=imagesURL%>/home.png" align="right" width="32" height="32" alt="Ir la men&uacute; principal" />
		</a>
		<h2>Carga de ficheros de pol&iacute;ticas de acceso</h2>
		 
		<form:form method="post" action="accessPolicyUpload.save" modelAttribute="uploadPolicyFiles" enctype="multipart/form-data">
		 
		    <p>Selecciona los ficheros de pol&iacute;ticas a cargar.</p>
		 
		    <table style="width: 100%;" id="fileTable">
		        <tr style="width: 100%;">
		            <td><input style="width: 100%;" name="files[0]" type="file" /></td>
		        </tr>
		        <tr style="width: 100%;">
		            <td><input style="width: 100%;" name="files[1]" type="file" /></td>
		        </tr>
		    </table>
		    <br/>
		    <p>Pulsa el bot&oacute;n A&ntilde;adir para agregar m&aacute;s ficheros.</p>
		    <input id="addFile" type="button" value="A&ntilde;adir archivo" />
		    <br/>
		    <div class="center">
		    	<button class="blue-pill" type="submit">Cargar</button>
		    </div>
		</form:form>
	</body>
</html>