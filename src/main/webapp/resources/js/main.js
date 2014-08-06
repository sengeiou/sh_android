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

function showFileSystem(path) {
	sendGetRequest('system/fileInformationList?path=' + path + '&parent=false&pattern=&directoriesOnly=false&filesOnly=false');
}

function showSystemMonitor() {
	sendGetRequest('system/monitor');
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

/**
 * Elimina por completo la caché del sistema, provocando que todas las peticiones entrantes se resuelvan contra el subsistema.
 */
function dropSystemCache() {
	modalAcceptanceShow('&iquest;Desea eliminar por completo la cach&eacute; del sistema?', 
			'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; la <strong>destrucci&oacute;n total</strong> de la configuraci&oacute;n de la cach&eacute, as&iacute; como las cach&eacutes activas.',
			function(){ sendGetRequest('cache/dropSystemCache'); });
}

/**
 * Abre el monitor de la caché indicada.
 * 
 * @param cacheManager Alias del manager que contiene la caché a monitorizar.
 * @param cache Alias de la caché a monitorizar.
 */
function showCacheMonitor(cacheManager, cache) {
	sendGetRequest('cache/monitor?cacheManager=' + cacheManager + '&cache=' + cache);
}
		
/**
 * Función que inicializa la caché indicada.
 * 
 * @param cacheManager Alias del manager que contiene la caché a incializar.
 * @param cache Alias de la caché a inicializar.
 */
function resetCache(cacheManager, cache) {
	modalAcceptanceShow('&iquest;Desea reiniciar la cach&eacute; ' + cache + ' del gestor de caché ' + cacheManager + '?',
		'La aceptaci&oacute;n de esta acci&oacute;n implicar&aacute; el <strong>vaciado total</strong> de la cach&eacute, produciendo un posible pico de accesos al subsistema.',
		function() {
			sendPostRequest({"cacheManager":cacheManager, "cache":cache}, 'cache/resetCache'); 
		});
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
