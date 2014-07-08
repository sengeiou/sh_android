// Gestiona la respuesta del servidor, y la muestra en el contenedor principal.
function onReadystatechangeHandler(event) {

	var contentObject = $("#mainContent");
	var status = null;

	try {
		status = event.target.status;
	}
	catch(e) {
		return;
	}

	if (status == '200' && event.target.responseText) {

		if (App.destructionFunction) {
			
			App.destructionFunction();
			
			App.destructionFunction = null;
		}
		
		contentObject.html(event.target.responseText);
	}
}

// Gestiona el inicio de la carga de datos.
function onLoadStartHandler(event) {

	$('#mainProgressBar-bar').css('width', '0%');
}

// Gestiona el fin de la carga de datos.
function onLoadEndHandler(event) {

	$('#mainProgressBar').hide();
}

// Gestiona el progreso.
function onProgressHandler(event) {

	if (event.lengthComputable) {

		var percent = (event.loaded/event.total)*100;
		$('#mainProgressBar-bar').css('width', percent + '%');

		if (event.loaded == event.total) {
			$('#mainProgressBar').hide();	
		}
		else {
			$('#mainProgressBar').show();
		}
	}
	else {
		$('#mainProgressBar').show();
	}
}

// Solicita el recurso indicado, pasando los parámetros en la misma URL del recurso. 
function sendGetRequest(resource) {

	onLoadStartHandler(null);

	var xhr = new XMLHttpRequest();

	// Asignación de las funciones asociadas a cada envento.
	xhr.onprogress = onProgressHandler;
	xhr.onloadend = onLoadEndHandler;
	xhr.addEventListener('readystatechange', onReadystatechangeHandler,	false);

	// Preparación de la request.
	xhr.open('GET', App.pagesURL + '/' + resource, false);

	// Envío.
	xhr.send();
}

// Envía los datos de formulario indicados, al recurso indicado. 
function sendPostRequest(formData, resource) {

	onLoadStartHandler(null);

	var xhr = new XMLHttpRequest();

	// Asignación de las funciones asociadas a cada envento.
	xhr.upload.addEventListener('loadstart', onLoadStartHandler, false);
	xhr.upload.addEventListener('progress', onProgressHandler, false);
	xhr.upload.addEventListener('load', onLoadEndHandler, false);
	xhr.onprogress = onProgressHandler;
	xhr.addEventListener('readystatechange', onReadystatechangeHandler,	false);

	// Preparación de la request.
	xhr.open('POST', App.pagesURL + '/' + resource, false);

	// Envío.
	xhr.send(formData);
}

// ====================================
// operaciones de los menús.
// ====================================

function showWorkload() {
	sendGetRequest('system/workload');
}

function showDataSourcesInformation() {
	sendGetRequest('dataSourcesInformation');
}

function showSystemContext() {
	sendGetRequest('system/context');
}

function showFileSystem() {
	sendGetRequest('system/fileInformationList?path=&parent=false&pattern=&directoriesOnly=false&filesOnly=false');
}

function showSystemMonitor() {
	sendGetRequest('system/monitor?period=1&timeRange=300'); // Se solicitan los últimos 5 minutos con resolución de un segundo.
}

function showAvailableEntities() {
	sendGetRequest('accesspolicy/availableEntities');
}

function uploadEntityPolicies() {
	sendGetRequest('accesspolicy/accessPolicyUpload.show');
}

function resetToDefaultPolicies() {
	modalAcceptanceShow('&iquest;Desea recargar las pol&iacute;ticas de acceso por defecto?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la p&eacute;rdida de todos los cambios aplicados manualmente, mediante la carga de ficheros de pol&iacute;ticas de acceso.',
			function(){ sendGetRequest('accesspolicy/loadDefault'); });
}

function dropAccessPolicies() {
	modalAcceptanceShow('&iquest;Desea eliminar pol&iacute;ticas activas?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; el <strong>bloqueo total</strong> del acceso a los servicios de datos.',
			function(){ sendGetRequest('accesspolicy/denyAll'); });
}

function showAvailableCacheManagers() {
	sendGetRequest('cache/availableCacheManagers');
}

function uploadCacheConfigutarions() {
	sendGetRequest('cache/cacheConfigurationUpload.show');
}

function loadDefaultCacheConfiguration() {
	modalAcceptanceShow('&iquest;Desea recargar la configuraci&oacute;n de cach&eacute; del sistema por defecto?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la p&eacute;rdida de todos los cambios aplicados manualmente, mediante la carga de ficheros de configuraci&oacute;n de cach&eacute;. <br/>' +
			+ 'Del mismo modo, se roper&aacute;n todas las cach&eacute;s.',
			function(){ sendGetRequest('cache/loadDefault'); });
}

function dropSystemCache() {
	modalAcceptanceShow('&iquest;Desea eliminar por completo la cach&eacute; del sistema?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la <strong>destrucci&oacute;n total</strong> de la configuraci&oacute;n de la cach&eacute, as&iacute; como las cach&eacutes activas.',
			function(){ sendGetRequest('cache/dropSystemCache'); });
}

function showAvailableHooks() {
	sendGetRequest('hook/availableHooks');
}

function showAvailableHooksDependencies() {
	sendGetRequest('hook/availableHooksDependencies');
}

function uploadHooks() {
	sendGetRequest('hook/hookUpload.show');
}

function resetToDefaultHooks() {
	modalAcceptanceShow('&iquest;Desea recargar los puntos de inserci&oacute;n (hooks) por defecto?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la p&eacute;rdida de todos los cambios aplicados manualmente, mediante la carga de fuentes de hooks.',
			function(){ sendGetRequest('hook/loadDefault'); });
}

function dropAvailableHooks() {
	modalAcceptanceShow('&iquest;Desea eliminar todos los puntos de inserci&oacute;n (hooks) del sistema?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la <strong>descarga</strong> de todos los puntos de inserci&oacute;n, y en consecuencia, puede provocar errores en algunos accesos a entidades que hagan referencia a agluno de los hooks cargados.',
			function(){ sendGetRequest('hook/dropAll'); });
}
