<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@include file="includes/locations.jsp" %>

<script>
$(document).ready(function() {
    // Añade una nueva fila para cargar otro fichero.
    $('#addFile').click(function() {
        var fileIndex = $('#fileList li').children().length;
        $('#fileList').append(
   		'<li class="list-group-item"><input style="width: 100%;" name="files['+ fileIndex +']" type="file" /></li>');
    });
});
</script>

<div class="panel panel-default">
	<div class="panel-heading">Carga de pol&iacute;ticas de acceso</div>
	<div class="panel-body">
		<p>Selecciona los ficheros de pol&iacute;ticas a cargar.</p>

		<!-- List group -->
		<form:form method="post" action="accessPolicyUpload.save" modelAttribute="uploadPolicyFiles" enctype="multipart/form-data">
			<ul id="fileList" class="list-group">
			       <li class="list-group-item"><input style="width: 100%;" name="files[0]" type="file" title="Seleccionar fichero"/></li>
			       <li class="list-group-item"><input style="width: 100%;" name="files[1]" type="file" /></li>
			</ul>
		</form:form>
		<input id="addFile" type="button" class="btn btn-default btn-sm" value="A&ntilde;adir archivo" />
	</div>
</div>
<div>
	<button type="button" class="center-block btn btn-warning btn-lg">Cargar ficheros de pol&iacute;ticas</button>
</div>
