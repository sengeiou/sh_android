<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>

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

// Elimina el fichero indicado, y renumera los que había después.
function removeFile(fileIndex) {
	
	console.log("removing " + fileIndex);
	
	var fileList = $("#fileList");
	var fileToRemove = $("#file-" + fileIndex);
	
	fileToRemove.remove();
	
	var numFiles = fileList.children().length;

	for (; fileIndex < numFiles; fileIndex++) {
		
		console.log(fileIndex);
		
		var file = $("#file-" + (fileIndex + 1));
		file.prop("id", "file-" + fileIndex);
		
		var fileInput = $("#file-input-" + (fileIndex + 1));
		fileInput.prop("id", "file-input-" + fileIndex);
		fileInput.prop("name", "files[" + fileIndex + "]");
		fileInput.off("change");
		fileInput.on("change", new Function("setFileName("+fileIndex+");"));
		
		var fileInfo = $("#file-info-" + (fileIndex + 1));
		fileInfo.prop("id", "file-info-" + fileIndex);
		
		var fileChooser = $("#file-chooser-" + (fileIndex + 1));
		fileChooser.prop("id", "file-chooser-" + fileIndex);
		fileChooser.prop("for", "file-input-" + fileIndex);
		
		var fileRemover = $("#file-remover-" + (fileIndex + 1));
		fileRemover.prop("id", "file-remover-" + fileIndex);
		fileRemover.off("click");
		fileRemover.on("click", new Function("removeFile("+fileIndex+");"));

		var fileToSetAsDefault = $("#file-as-default-" + (fileIndex + 1));
		fileToSetAsDefault.prop("id", "file-as-default-" + fileIndex);
		fileToSetAsDefault.off("click");
		fileToSetAsDefault.on("click", new Function("toggleFileAsDefault("+fileIndex+");"));
		
		var fileInputToSetAsDefault = $("#file-input-as-default-" + (fileIndex + 1));
		fileInputToSetAsDefault.prop("id", "file-input-as-default-" + fileIndex);
		fileInputToSetAsDefault.prop("name", "filesAsDefault[" + fileIndex + "]");
	}
}

//Marca/Desmarca el fichero indicado, como fichero para establecer por defecto.
function toggleFileAsDefault(fileIndex) {
	
	console.log("toggle file as default " + fileIndex);
	
	var fileToSetAsDefault = $("#file-as-default-" + fileIndex);
	var fileInputToSetAsDefault = $("#file-input-as-default-" + fileIndex);
	var spanIcon = fileToSetAsDefault.children("span");
		
	//Si no es por defecto, contiene la clase: btn-default
	//Si es por defecto, contiene la clase: btn-primary
	if (fileToSetAsDefault.hasClass("btn-default")) {
		
		fileToSetAsDefault.addClass("btn-primary");
		fileToSetAsDefault.removeClass("btn-default");
		fileInputToSetAsDefault.prop("checked", true);
		spanIcon.removeClass("glyphicon-star-empty");
		spanIcon.addClass("glyphicon-star");
	}
	else if (fileToSetAsDefault.hasClass("btn-primary")) {
		
		fileToSetAsDefault.addClass("btn-default");
		fileToSetAsDefault.removeClass("btn-primary");
		fileInputToSetAsDefault.prop("checked", false);
		spanIcon.removeClass("glyphicon-star");
		spanIcon.addClass("glyphicon-star-empty");
	}
}

// Añade una nueva fila para cargar otro fichero.
function addFile() {
	var fileIndex;
	var numFiles = $("#fileList").children().length;
	
	if (numFiles == 0) {
		fileIndex = 0;
	}
	else {
     var lastId = $("#fileList").children().last().prop("id");
     var fileIndex = lastId.replace("file-", "");
     fileIndex ++;
	}
    
    var newFileSelectionLine = "<li id='file-" + fileIndex + "' class='list-group-item'>";
    newFileSelectionLine += "<input id='file-input-" + fileIndex + "' type='file' name='files[" + fileIndex + "]' tabindex='-1' style='position: absolute; z-index:-1;'/>";
    newFileSelectionLine += "<div style='height: 30px'>";

    newFileSelectionLine += "<label id='file-info-" + fileIndex + "' class='label label-default vcenter'></label>";
    newFileSelectionLine += "<label id='file-chooser-" + fileIndex + "' for='file-input-" + fileIndex + "' class='btn btn-sm glyphicon glyphicon-folder-open'><span style='padding-left: 12px;'>Seleccionar</span></label>";
    newFileSelectionLine += "<label id='file-remover-" + fileIndex + "' class='btn btn-sm glyphicon glyphicon-trash vcenter'><span></span></label>";
    
    newFileSelectionLine += "<button id='file-as-default-" + fileIndex + "' type='button' class='btn btn-default btn-sm pull-right'>";
    newFileSelectionLine += "<span class='glyphicon glyphicon-star-empty'></span>";
    newFileSelectionLine += "Establecer por defecto";
    newFileSelectionLine += "<input id='file-input-as-default-" + fileIndex + "' type='checkbox' name='filesAsDefault[" + fileIndex + "]' style='position: absolute; z-index:-1;'/>"; 
    newFileSelectionLine += "</button>";

    newFileSelectionLine += "</div>";
    newFileSelectionLine += "</li>";
    
    $("#fileList").append(newFileSelectionLine);
    
    $("#file-input-" + fileIndex).on("change", function() {setFileName(fileIndex);});
    $("#file-remover-" + fileIndex).on("click", function() {removeFile(fileIndex);});
    $("#file-as-default-" + fileIndex).on("click", function() {toggleFileAsDefault(fileIndex);});
}

// Ejecuciones al final de la carga del documento.
$(document).ready(function() {

	//Se Añade el primer fichero a cargar.
	addFile();

	//Se asocia el click del botón de Añadir fichero, a la función de Añadir.
    $("#addFile").click(function() {addFile();});
    
	//Se asocia el click del botón de enviar datos del formulario.
	$("#uploadButton").on("click", function() {
		var dataToServer = new FormData(document.getElementById("uploadForm"));
		
		sendPostRequest(dataToServer, "cache/cacheConfigurationUpload.save");

		return false;
	});
});
</script>

<form:form id="uploadForm" class="form-inline" method="post" modelAttribute="uploadCacheConfigurationFiles" enctype="multipart/form-data">
<div class="panel panel-default">
	<div class="panel-heading">Carga de configuraciones de cach&eacute;</div>
	<div class="panel-body">
		<p>Selecciona los ficheros de configuraci&oacute;n de cach&eacute; a cargar.</p>
		<!-- List group -->
			<ul id="fileList" class="list-group">
			</ul>
		<input id="addFile" type="button" class="btn btn-default btn-sm" value="A&ntilde;adir archivo" />
	</div>
</div>
<div>
	<button id="uploadButton" type="button" class="center-block btn btn-warning btn-lg">Cargar ficheros de configuraci&oacute;n de cach&eacute;</button>
</div>
</form:form>
