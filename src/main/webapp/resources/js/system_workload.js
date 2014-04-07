/**
 * Estructura global del monitor.
 */
var WorkloadMonitor = {
		RequestsRate : null,
		RequestsRatePeak : null,
		TotalRequests : null,
		TotalRequestsKo : null,
		OperationRate : null,
		OperationRatePeak : null,
		TotalOperations : null,
		TotalOperationsKo : null,
		SubsystemOperationRate : null,
		SubsystemOperationRatePeak : null,
		TotalSubsystemOperations : null,
		TotalSubsystemOpertionsKo : null
}

/**
 * Inicializa el monitor de trabajo realizado.
 * 
 * @param RequestsRate Etiqueta que contiene la tasa de peticiones entrantes.
 * @param RequestsRatePeak Etiqueta que contiene el pico máximo de tasa de peticiones entrantes.
 * @param TotalRequests Etiqueta que contiene el número total de peticiones entrantes.
 * @param TotalRequestsKo Etiqueta que contiene el número total de peticiones entrantes rechazadas.
 * @param OperationRate Etiqueta que contiene la tasa de operaciones procesadas.
 * @param OperationRatePeak Etiqueta que contiene el pico máximo de tasa de operaciones procesadas.
 * @param TotalOperations Etiqueta que contiene las operaciones procesadas.
 * @param TotalOperationsKo Etiqueta que contiene las operaciones rechazadas.
 * @param SubsystemOperationRate Etiqueta que contiene la tasa de operaciones enviadas al subsistema.
 * @param SubsystemOperationRatePeak Etiqueta que contiene el pico máximo de tasa de operaciones enviadas al subsistema.
 * @param TotalSubsystemOperations Etiqueta que contiene el número total de operaciones enviadas al subsistema.
 * @param TotalSubsystemOpertionsKo Etiqueta que contiene el número total de operaciones fallidas en el subsistema.
 */
function initSystemWorkload(
		RequestsRate, RequestsRatePeak, TotalRequests, TotalRequestsKo,
		OperationRate, OperationRatePeak, TotalOperations,TotalOperationsKo,
		SubsystemOperationRate, SubsystemOperationRatePeak, TotalSubsystemOperations, TotalSubsystemOpertionsKo) {

	WorkloadMonitor.RequestsRate = RequestsRate;
	WorkloadMonitor.RequestsRatePeak = RequestsRatePeak;
	WorkloadMonitor.TotalRequests = TotalRequests;
	WorkloadMonitor.TotalRequestsKo = TotalRequestsKo;
	WorkloadMonitor.OperationRate = OperationRate;
	WorkloadMonitor.OperationRatePeak = OperationRatePeak;
	WorkloadMonitor.TotalOperations = TotalOperations;
	WorkloadMonitor.TotalOperationsKo = TotalOperationsKo;
	WorkloadMonitor.SubsystemOperationRate = SubsystemOperationRate;
	WorkloadMonitor.SubsystemOperationRatePeak = SubsystemOperationRatePeak;
	WorkloadMonitor.TotalSubsystemOperations = TotalSubsystemOperations;
	WorkloadMonitor.TotalSubsystemOpertionsKo = TotalSubsystemOpertionsKo;

	startWorkloadMonitor();
	
	// Asignación de la función de destrución de los procesos activos.
	App.destructionFunction = destroySystemWorkload;
}

/**
 * Función instanciada al cerrar la ventana de monitorización del trabajo realizado por el sistema.
 */
function destroySystemWorkload() {

	stopWorkloadMonitor();

	delete WorkloadMonitor;
}

/**
 * Alimentador de información del diagrama de trabajo realizado instantaneo.
 */
function workloadMonitorDataRenderer() {

	delete data;

	var xhr = new XMLHttpRequest();

	xhr.open("POST", App.servicesURL + '/system/workload', false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({offset: null, period: null, timeRange: null}));

	var jsonResponse = JSON.parse(xhr.responseText);

	// Asignación de la información a las etiquetas.
	WorkloadMonitor.RequestsRate.innerHTML = Math.floor(jsonResponse["data"]["RequestsRate"]).toPrecision(2).toLocaleString() + " req/s";
	WorkloadMonitor.RequestsRatePeak.innerHTML = Math.floor(jsonResponse["data"]["RequestsRatePeak"]).toPrecision(2).toLocaleString() + " req/s";
	WorkloadMonitor.TotalRequests.innerHTML = jsonResponse["data"]["TotalRequests"].toLocaleString() + " req";
	WorkloadMonitor.TotalRequestsKo.innerHTML = jsonResponse["data"]["TotalRequestsKo"].toLocaleString() + " req";
	WorkloadMonitor.OperationRate.innerHTML = Math.floor(jsonResponse["data"]["OperationRate"]).toPrecision(2).toLocaleString() + " op/s";
	WorkloadMonitor.OperationRatePeak.innerHTML = Math.floor(jsonResponse["data"]["OperationRatePeak"]).toPrecision(2).toLocaleString() + " op/s";
	WorkloadMonitor.TotalOperations.innerHTML = jsonResponse["data"]["TotalOperations"].toLocaleString() + " op";
	WorkloadMonitor.TotalOperationsKo.innerHTML = jsonResponse["data"]["TotalOperationsKo"].toLocaleString() + " op";
	WorkloadMonitor.SubsystemOperationRate.innerHTML = Math.floor(jsonResponse["data"]["SubsystemOperationRate"]).toPrecision(2).toLocaleString() + " op/s";
	WorkloadMonitor.SubsystemOperationRatePeak.innerHTML = Math.floor(jsonResponse["data"]["SubsystemOperationRatePeak"]).toPrecision(2).toLocaleString() + " op/s";
	WorkloadMonitor.TotalSubsystemOperations.innerHTML = jsonResponse["data"]["TotalSubsystemOperations"].toLocaleString() + " op";
	WorkloadMonitor.TotalSubsystemOpertionsKo.innerHTML = jsonResponse["data"]["TotalSubsystemOpertionsKo"].toLocaleString() + " op";
}

/**
 * Detiene/Reanuda el monitor de trabajo realizado en función del parámetro de entrada.
 *  
 * @param freeze True o false para detener/reanudar el monitor de trabajo realizado.
 */
function freezeWorkloadMonitor(freeze) {

	if (freeze) {
		stopWorkloadMonitor();
	} else {
		startWorkloadMonitor();
	}
}

/**
 * Detiene el monitor de trabajo realizado.
 */
function stopWorkloadMonitor() {

	if (App.workloadMonitorInterval) {
		clearInterval(App.workloadMonitorInterval);
		App.workloadMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de trabajo realizado.
 */
function startWorkloadMonitor() {

	stopWorkloadMonitor();
	App.workloadMonitorInterval = setInterval(function() { 		

		// Petición y repintado de la información de trabajo realizado por el sistema.
		workloadMonitorDataRenderer();

	}, 1000);
}