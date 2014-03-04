<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@include file="includes/locations.jsp" %>

<script type="text/javascript">

// Asigna el nombre del archivo a cargar.
function setFileName (fileIndex) {
	var file = $("#file-input-" + fileIndex).val(); 

	var dirSeparator = file.lastIndexOf('/');
	
	if (dirSeparator == -1) {
		dirSeparator = file.lastIndexOf('\\');
	}
	
	if (dirSeparator != -1) {
		
		file = file.substring(dirSeparator+1, file.length); 
		$("#file-info-" + fileIndex).html(file);
	}
}

// Elimina el fichero indicado, y renumera los que hab�a despu�s.
function removeFile(fileIndex) {
	
	var fileList = $('#fileList');
	var fileToRemove = $('#file-' + fileIndex);
	
	fileToRemove.remove();
	
	var numFiles = fileList.children().length;
	
	for(;fileIndex < numFiles; fileIndex++) {
		var file = $('#file-' + (fileIndex + 1));
		file.attr("id", "file-" + fileIndex);
		var fileInput = $('#file-input-' + (fileIndex + 1));
		fileInput.attr("id", "file-input-" + fileIndex);
		fileInput.attr("name", "files[" + fileIndex + "]");
		fileInput.on("change", "setFileName(" + fileIndex + ");");
		var fileInfo = $('#file-info-' + (fileIndex + 1));
		fileInfo.attr("id", "file-info-" + fileIndex);
		var fileChooser = $('#file-chooser-' + (fileIndex + 1));
		fileChooser.attr("id", "file-chooser-" + fileIndex);
		fileChooser.attr("for", "file-input-" + fileIndex);
		var fileRemover = $('#file-remover-' + (fileIndex + 1));
		fileRemover.attr("id", "file-remover-" + fileIndex);
		fileRemover.on("click", "removeFile(" + fileIndex + ");");
	}
}

// A�ade una nueva fila para cargar otro fichero.
function addFile() {
	var fileIndex;
	var numFiles = $('#fileList').children().length;
	
	if (numFiles == 0) {
		fileIndex = 0;
	}
	else {
     var lastId = $('#fileList').children().last().attr("id");
     var fileIndex = lastId.replace("file-", "");
     fileIndex ++;
	}
    
    var newFileSelectionLine = "<li id='file-" + fileIndex + "' class='list-group-item'>";
    newFileSelectionLine += "<input id='file-input-" + fileIndex + "' type='file' name='files[" + fileIndex + "]' tabindex='-1' style='position: absolute; z-index:-1;' onchange='setFileName(" + fileIndex + ");'/>";
    newFileSelectionLine += "<div>";
    newFileSelectionLine += "<label id='file-info-" + fileIndex + "' class='label label-default vcenter'></label>";
    newFileSelectionLine += "<label id='file-chooser-" + fileIndex + "' for='file-input-" + fileIndex + "' class='btn btn-sm glyphicon glyphicon-folder-open'><span style='padding-left: 12px;'>Seleccionar</span></label>";
    newFileSelectionLine += "<label id='file-remover-" + fileIndex + "' class='btn btn-sm glyphicon glyphicon-trash vcenter' onclick='removeFile(" + fileIndex + ");'><span></span></label>";
    newFileSelectionLine += "</div>";
    newFileSelectionLine += "</li>";
    
    $('#fileList').append(newFileSelectionLine);
}

// Ejecuciones al final de la carga del documento.
$(document).ready(function() {

	//Se a�ade el primer fichero a cargar.
	addFile();

	//Se asocia el click del bot�n de a�adir fichero, a la funci�n de a�adir.
    $('#addFile').click(function() {addFile();});
    
	//Se asocia el click del bot�n de enviar datos del formulario.
	$('#uploadButton').on('click', function(){
		var dataToServer = new FormData(document.getElementById('uploadForm'));
		
		sendPostRequest(dataToServer, 'accesspolicy/accessPolicyUpload.save');

		return false;
	});
});
</script>

<form:form id="uploadForm" class="form-inline" method="post" modelAttribute="uploadPolicyFiles" enctype="multipart/form-data">
<div class="panel panel-default">
	<div class="panel-heading">Carga de pol&iacute;ticas de acceso</div>
	<div class="panel-body">
		<p>Selecciona los ficheros de pol&iacute;ticas a cargar.</p>
		<!-- List group -->
			<ul id="fileList" class="list-group">
			</ul>
		<input id="addFile" type="button" class="btn btn-default btn-sm" value="A&ntilde;adir archivo" />
	</div>
</div>
<div>
	<button id="uploadButton" type="button" class="center-block btn btn-warning btn-lg">Cargar ficheros de pol&iacute;ticas</button>
</div>
</form:form>
