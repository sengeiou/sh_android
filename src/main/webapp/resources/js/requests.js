/**
 * Gestiona la respuesta del servidor, y la muestra en el contenedor principal.
 * 
 * @param event Evento que contiene la respuesta de la petición.
 */
function defaultCallback(event) {

	if (event.srcElement.readyState === 4) {

		var contentObject = $("#mainContent");
		var status = null;

		try {
			status = event.target.status;
		}
		catch(e) {
			return;
		}

		if (status == "200" && event.target.responseText != undefined) {

			if (App.destructionFunction) {

				App.destructionFunction();

				App.destructionFunction = null;
			}

			contentObject.html(event.target.responseText);
		}
	}
}

/**
 * Función de retrollamada para las peticiones que retornan un objeto de tipo ResultDto, cuyo
 * contenido debe ser representado en forma de alert.
 * 
 * @param event Evento de retorno de la petición.
 */
function alertCallback(event) {

	if (event.srcElement.readyState === 4) {

		var alertContainer = $("#alertContainer");

		var currentId = parseInt(alertContainer.attr("data-alert-count"));

		var nextId = currentId + 1;
		var htmlAlert = null;

		var status = null;

		try {
			status = event.target.status;

			if (status == "200") {

				var result = JSON.parse(event.target.responseText);

				if (result.resultCode == "OK") {
					htmlAlert = "<div id='alert-" + nextId + "' class='alert alert-success' role='alert'>";

					if (result.message != undefined) {
						htmlAlert += result.message;

						if (result.explanation != undefined) {
							htmlAlert += "<br/>" + result.explanation; 
						}
					}
					else if (result.explanation != undefined) {
						htmlAlert += result.explanation; 
					}

					htmlAlert += "</div>"; 
				}
				else {
					htmlAlert = "<div id='alert-" + nextId + "' class='alert alert-danger' role='alert'>";

					if (result.resultCode != undefined) {
						htmlAlert += result.resultCode;

						if (result.message != undefined) {
							htmlAlert += "  " + result.message; 

							if (result.explanation != undefined) {
								htmlAlert += "<br/>" + result.explanation; 
							}
						}
						else if (result.explanation != undefined) {
							htmlAlert += "<br/>" + result.explanation; 
						}
					}
					else if (result.message != undefined) {
						htmlAlert += result.message;

						if (result.explanation != undefined) {
							htmlAlert += "<br/>" + result.explanation; 
						}
					}
					else if (result.explanation != undefined) {
						htmlAlert += result.explanation; 
					}

					htmlAlert += "</div>"; 
				}
			}
			else {
				htmlAlert = "<div id='alert-" + nextId + "' class='alert alert-danger' role='alert'>";
				htmlAlert += "Error " + status + " en la llamada.";
				htmlAlert += "</div>";
			}
		}
		catch(e) {

			htmlAlert = "<div id='alert-" + nextId + "' class='alert alert-danger' role='alert'> No ja sido posible ejecutar la operación debido a: " + e.message + "</div>";
		}

		alertContainer.append(htmlAlert);
		alertContainer.attr("data-alert-count", nextId);
		
		setTimeout(function(){
			$("#alert-" + nextId).fadeOut(1000);
		}, 3000);
	}
}

/**
 * Gestiona el inicio de la carga de datos.
 */
function onLoadStartHandler(event) {
	
	$("#mainProgressBar-bar").attr("aria-aria-valuemax", 100);
	$("#mainProgressBar-bar").attr("aria-valuemin", 0);

	$("#mainProgressBar-bar").attr("aria-valuenow", 0);
	$("#mainProgressBar-bar").css("width", "0%");
}

/**
 * Gestiona el fin de la carga de datos.
 */
function onLoadEndHandler(event) {
	
	$("#mainProgressBar").hide();
}

/**
 * Gestiona el progreso.
 */
function onProgressHandler(event) {

	if (event.lengthComputable) {

		var percent = (event.loaded/event.total)*100;
		$("#mainProgressBar-bar").css("width", percent + "%");
		$("#mainProgressBar-bar").attr("aria-valuenow", percent);

		if (event.loaded == event.total) {
			$("#mainProgressBar").hide();
		}
		else {
			$("#mainProgressBar").show();
		}
	}
	else {
		$("#mainProgressBar").hide();
	}
}

/**
 * Solicita el recurso indicado, pasando los parámetros en la misma URL del recurso.
 * 
 * @param resource Recurso a consultar.
 * @param isAlert True o false en función de si el resultado de la llamada se resuelve con un alert o se resuelve sustituyendo el contenido general de la página.
 */
function sendGetRequest(resource, isAlert) {

	var xhr = new XMLHttpRequest();
	
	// Asignación de las funciones asociadas a cada envento.
	xhr.addEventListener("loadstart", onLoadStartHandler, false);
	xhr.addEventListener("progress", onProgressHandler, false);
	xhr.addEventListener("loadend", onLoadEndHandler, false);
	
	if (isAlert != undefined && isAlert == true) {
		xhr.addEventListener("readystatechange", alertCallback,	false);
	}
	else {
		xhr.addEventListener("readystatechange", defaultCallback, false);
	}

	// Preparación de la request.
	xhr.open("GET", App.pagesURL + "/" + resource, false);

	// Envío.
	xhr.send();
}

/**
 * Solicita el recurso indicado, pasando los parámetros en forma de JSON.
 * 
 * @param data Datos a enviar.
 * @param resource Recurso a consultar.
 * @param isAlert True o false en función de si el resultado de la llamada se resuelve con un alert o se resuelve sustituyendo el contenido general de la página.
 */
function sendPostRequest(data, resource, isAlert) {

	var xhr = new XMLHttpRequest();

	// Asignación de las funciones asociadas a cada envento.
	xhr.addEventListener("loadstart", onLoadStartHandler, false);
	xhr.addEventListener("progress", onProgressHandler, false);
	xhr.addEventListener("loadend", onLoadEndHandler, false);

	if (isAlert != undefined && isAlert) {
		xhr.addEventListener("readystatechange", alertCallback,	false);
	}
	else {
		xhr.addEventListener("readystatechange", defaultCallback,	false);
	}

	var dataString = JSON.stringify(data);
	
	// Preparación de la request.
	xhr.open("POST", App.pagesURL + "/" + resource);
	xhr.setRequestHeader("Content-Type", "application/json; charset=utf-8");

	// Envío.
	xhr.send(dataString);
}

/**
 * Solicita el recurso indicado, pasando los parámetros en forma de datos de formulario.
 * 
 * @param data Datos a enviar.
 * @param resource Recurso a consultar.
 * @param isAlert True o false en función de si el resultado de la llamada se resuelve con un alert o se resuelve sustituyendo el contenido general de la página.
 */
function sendPostFormRequest(data, resource, isAlert) {

	var xhr = new XMLHttpRequest();
	
	// Asignación de las funciones asociadas a cada envento.
	xhr.addEventListener("loadstart", onLoadStartHandler, false);
	xhr.addEventListener("progress", onProgressHandler, false);
	xhr.addEventListener("loadend", onLoadEndHandler, false);
	
	if (isAlert != undefined) {
		xhr.addEventListener("readystatechange", alertCallback,	false);
	}
	else {
		xhr.addEventListener("readystatechange", defaultCallback,	false);
	}

	// Preparación de la request.
	xhr.open("POST", App.pagesURL + "/" + resource, false);

	// Envío.
	xhr.send(data);
}
