/**
 * Estructura global del monitor.
 */
var WorkloadMonitor = {
		servicesURL: null,
		
		RequestsRate : null,
		RequestsRatePeak : null,
		TotalRequests : null,
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
 * @param servicesURL Dirección base de las llamadas a los servicios de monitoreo.
 * 
 * @param RequestsRate Etiqueta que contiene la tasa de peticiones entrantes.
 * @param RequestsRatePeak Etiqueta que contiene el pico máximo de tasa de peticiones entrantes.
 * @param TotalRequests Etiqueta que contiene el número total de peticiones entrantes.
 * @param OperationRate Etiqueta que contiene la tasa de operaciones procesadas.
 * @param OperationRatePeak Etiqueta que contiene el pico máximo de tasa de operaciones procesadas.
 * @param TotalOperations Etiqueta que contiene las operaciones procesadas.
 * @param TotalOperationsKo Etiqueta que contiene las operaciones rechazadas.
 * @param SubsystemOperationRate Etiqueta que contiene la tasa de operaciones enviadas al subsistema.
 * @param SubsystemOperationRatePeak Etiqueta que contiene el pico máximo de tasa de operaciones enviadas al subsistema.
 * @param TotalSubsystemOperations Etiqueta que contiene el número total de operaciones enviadas al subsistema.
 * @param TotalSubsystemOpertionsKo Etiqueta que contiene el número total de operaciones fallidas en el subsistema.
 */
function initWorkloadMonitor(servicesURL, 
		RequestsRate, RequestsRatePeak, TotalRequests,
		OperationRate, OperationRatePeak, TotalOperations,TotalOperationsKo,
		SubsystemOperationRate, SubsystemOperationRatePeak, TotalSubsystemOperations, TotalSubsystemOpertionsKo) {

	WorkloadMonitor.servicesURL = servicesURL;
	
	WorkloadMonitor.RequestsRate = RequestsRate;
	WorkloadMonitor.RequestsRatePeak = RequestsRatePeak;
	WorkloadMonitor.TotalRequests = TotalRequests;
	WorkloadMonitor.OperationRate = OperationRate;
	WorkloadMonitor.OperationRatePeak = OperationRatePeak;
	WorkloadMonitor.TotalOperations = TotalOperations;
	WorkloadMonitor.TotalOperationsKo = TotalOperationsKo;
	WorkloadMonitor.SubsystemOperationRate = SubsystemOperationRate;
	WorkloadMonitor.SubsystemOperationRatePeak = SubsystemOperationRatePeak;
	WorkloadMonitor.TotalSubsystemOperations = TotalSubsystemOperations;
	WorkloadMonitor.TotalSubsystemOpertionsKo = TotalSubsystemOpertionsKo;

	stopWorkloadMonitor();
}

/**
 * Alimentador de información del diagrama de trabajo realizado instantaneo.
 */
function workloadMonitorDataRenderer() {

	delete data;

	var xhr = new XMLHttpRequest();

	xhr.open("POST", WorkloadMonitor.servicesURL + '/system/workload', false);
	xhr.setRequestHeader("Content-Type", "application/json;charset=UTF-8");
	// Envío.
	xhr.send(JSON.stringify({offset: null, period: null, timeRange: null}));

	var jsonResponse = JSON.parse(xhr.responseText);

	// Asignación de la información a las etiquetas.
	WorkloadMonitor.RequestsRate.innerHTML = jsonResponse["data"]["RequestsRate"] + " req/s";
	WorkloadMonitor.RequestsRatePeak.innerHTML = jsonResponse["data"]["RequestsRatePeak"] + " req/s";
	WorkloadMonitor.TotalRequests.innerHTML = jsonResponse["data"]["TotalRequests"] + " req";
	WorkloadMonitor.OperationRate.innerHTML = jsonResponse["data"]["OperationRate"] + " op/s";
	WorkloadMonitor.OperationRatePeak.innerHTML = jsonResponse["data"]["OperationRatePeak"] + " op/s";
	WorkloadMonitor.TotalOperations.innerHTML = jsonResponse["data"]["TotalOperations"] + " op";
	WorkloadMonitor.TotalOperationsKo.innerHTML = jsonResponse["data"]["TotalOperationsKo"] + " op";
	WorkloadMonitor.SubsystemOperationRate.innerHTML = jsonResponse["data"]["SubsystemOperationRate"] + " op/s";
	WorkloadMonitor.SubsystemOperationRatePeak.innerHTML = jsonResponse["data"]["SubsystemOperationRatePeak"] + " op/s";
	WorkloadMonitor.TotalSubsystemOperations.innerHTML = jsonResponse["data"]["TotalSubsystemOperations"] + " op";
	WorkloadMonitor.TotalSubsystemOpertionsKo.innerHTML = jsonResponse["data"]["TotalSubsystemOpertionsKo"] + " op";
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

	if (this.WorkloadMonitorInterval) {
		clearInterval(this.WorkloadMonitorInterval);
		this.WorkloadMonitorInterval = null;
	}
}

/**
 * Reanuda el monitor de trabajo realizado.
 */
function startWorkloadMonitor() {

	stopWorkloadMonitor();
	this.WorkloadMonitorInterval = setInterval(function() { 		

		// Petición y repintado de la información de trabajo realizado por el sistema.
		workloadMonitorDataRenderer();

	}, 1000);
}