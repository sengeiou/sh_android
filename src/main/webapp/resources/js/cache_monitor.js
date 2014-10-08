/**
 * Estructura global de la caché a consultar y la ventana de tiempo de datos se desea obtener.
 */
CacheMonitorWindow = function(cacheManager, cache) {

	/**
	 * Función de refresco de la ventana temporal de monitorización.
	 */
	updateCacheMonitorWindow = function(period, from, to) {

		if (from != null) {
			from = from.getTime()/1000;
		}
		else {
			from = null;
		}

		if (to != null) {
			to = to.getTime()/1000;
		}
		else {
			to = null;
		}

		this.period = period;
		this.offset = from;
		this.nextOffset = null;
		this.timeRange = (from == null || to == null) ? null : to - from;
	}

	/**
	 * Retorna la cadena JSON para una petición correspondiente a la ventana actual de monitorización.
	 */
	getWindowRequestJSON = function() {

		var requestObject = {
				period: this.period,
				offset: this.nextOffset == null ? this.offset : this.nextOffset,
						timeRange: this.nextOffset == null ? this.timeRange : this.period,
								cacheManager: this.cacheManager,
								cache: this.cache
		};

		return JSON.stringify(requestObject);
	}

	/**
	 * Incrementa el siguiente offset 1 periodo.
	 */
	moveToNextPeriod = function() {

		if (this.nextOffset == null) {
			this.nextOffset = this.offset;
		}
		else {
			this.nextOffset += this.period;
		}
	}

	/**
	 * Retorna el siguiente offset.
	 */
	getNextOffset = function() {

		return this.nextOffset == null ? this.offset : this.nextOffset;
	}

	return {
		period: null,
		offset: null,
		nextOffset: null,
		timeRange: null,
		cacheManager: cacheManager,
		cache: cache,

		updateCacheMonitorWindow: updateCacheMonitorWindow,
		getNextOffset: getNextOffset,
		moveToNextPeriod: moveToNextPeriod,
		getWindowRequestJSON: getWindowRequestJSON
	};
}

/**
 * Aplica los cambios de selección temporal.
 * 
 * @param periodSelector Selector que contiene el periodo a aplicar.
 * @param segmentSelector Selector que contiene el segmento a refrescar.
 * @param datetimeFrom Selector de momento de inicio a aplicar.
 * @param datetimeTo Selector momento de fin a aplicar.
 * @param applyTimeWindow Botón que aplica los cambios de configuración de la ventana de tiempo.
 */
function applyCacheMonitorChange(periodSelector, segmentSelector, datetimeFrom, datetimeTo, applyTimeWindow) {

	var period = parseInt(periodSelector.val());
	var segment = parseInt(segmentSelector.val());
	var from;
	var to;

	stopCacheMonitor();

	if (segment == -1) {

		from = new Date(datetimeFrom.data("DateTimePicker").getDate().unix() * 1000);
		to = new Date(datetimeTo.data("DateTimePicker").getDate().unix() * 1000);
	}
	else {

		to = new Date();
		from = new Date(to.getTime() - segment * 1000);
	}

	if (to != null && from != null) {

		cacheMonitorWindow.updateCacheMonitorWindow(period, from, to);
		cacheMonitorRequestWindow(cacheMonitorWindow, false);

		if (segment != -1) {
			startCacheMonitor(cacheMonitorWindow);
		}
	}

	applyTimeWindow.addClass("disabled");
}

/**
 * Función de inicialización del monitor del sistema.
 * 
 * @param cacheManager Nombre del gestor de cachés que contiene la cahé a monitorizar. 
 * @param cache Nombre de la cahé a monitorizar.
 * @param maxBytesLocalHeap Tamaño máximo de la caché en memoria, expresado en bytes.
 * @param maxBytesLocalDisk Tamaño máximo de la caché en disco, expresado en bytes.
 *  
 * @param periodSelector Nombre del elemento que contiene el periodo de refresco. 
 * @param segmentSelector Nombre del elemento que contiene el segmento a refrescar. 
 * @param datetimePickerAccessFrom Nombre del elemento que contiene el momento inicial del intérvalo a mostrar.
 * @param datetimePickerAccessTo Nombre del elemento que contiene el momento final del intérvalo a mostrar.
 * @param applyTimeWindowButton Nombre del botón que aplica los cambios de configuración de la ventana de tiempo.
 * 
 * @param cacheHAHeapHistory Nombre del elemento en el que se renderiza el histórico de proporciones entre aciertos y fallos de caché en memoria.
 * @param cacheHAHeap Nombre del elemento en el que se renderiza el porcentaje de aciertos y fallos de caché en memoria.
 * @param cacheHADiskHistory Nombre del elemento en el que se renderiza el histórico de proporciones entre aciertos y fallos de caché en disco.
 * @param cacheHADisk Nombre del elemento en el que se renderiza el porcentaje de aciertos y fallos de caché en disco.
 * @param cacheHeapHistory Nombre del elemento en el que se renderiza el histórico de uso de memoria.
 * @param cacheHeap Nombre del elemento en el que se renderiza el uso de memoria.
 * @param cacheDiskHistory Nombre del elemento en el que se renderiza el histórico de uso de disco.
 * @param cacheDisk Nombre del elemento en el que se renderiza el uso de disco.
 */
function initCacheMonitor(cacheManager, cache, maxBytesLocalHeap, maxBytesLocalDisk,
		periodSelector, segmentSelector, datetimePickerAccessFrom, datetimePickerAccessTo, applyTimeWindowButton,
		cacheHAHeapHistory, cacheHAHeap, 
		cacheHADiskHistory, cacheHADisk, 
		cacheHeapHistory, cacheHeap, 
		cacheDiskHistory, cacheDisk
)
{
	cacheMonitorWindow = CacheMonitorWindow(cacheManager, cache);

	period = $("#" + periodSelector);
	segment = $("#" + segmentSelector);
	datetimeFrom = $("#" + datetimePickerAccessFrom);
	datetimeTo = $("#" + datetimePickerAccessTo);
	removeDatetimeTo = datetimeTo.find("span.remove");
	applyTimeWindow = $("#" + applyTimeWindowButton);

	period.on("change", function (e) {

		applyTimeWindow.removeClass("disabled");
	});

	segment.on("change", function (e) {

		var segmentValue = parseInt(segment.val());

		if (segmentValue == -1) {

			datetimeFrom.data("DateTimePicker").enable();
			datetimeTo.data("DateTimePicker").enable();
		}
		else {
			datetimeFrom.data("DateTimePicker").disable();
			datetimeTo.data("DateTimePicker").disable();
		}

		applyTimeWindow.removeClass("disabled");
	});

	datetimeFrom.datetimepicker({
		language: "es",
		pick12HourFormat: false,
		pickDate: true,                 //en/disables the date picker
		pickTime: true,                 //en/disables the time picker
		useMinutes: true,               //en/disables the minutes picker
		useSeconds: true,               //en/disables the seconds picker
		useCurrent: true,               //when true, picker will set the value to the current date/time     
		icons: {
			time: "glyphicon glyphicon-time",
			date: "glyphicon glyphicon-calendar",
			up: "glyphicon glyphicon-chevron-up",
			down: "glyphicon glyphicon-chevron-down"
		}
	});

	datetimeTo.datetimepicker({
		language: "es",
		pick12HourFormat: false,
		pickDate: true,                 //en/disables the date picker
		pickTime: true,                 //en/disables the time picker
		useMinutes: true,               //en/disables the minutes picker
		useSeconds: true,               //en/disables the seconds picker
		useCurrent: true,               //when true, picker will set the value to the current date/time     
		icons: {
			time: "glyphicon glyphicon-time",
			date: "glyphicon glyphicon-calendar",
			up: "glyphicon glyphicon-chevron-up",
			down: "glyphicon glyphicon-chevron-down"
		}
	});

	datetimeFrom.on("dp.change", function (e) {

		if (e.date != null) {

			datetimeTo.data("DateTimePicker").setMinDate(e.date);
			applyTimeWindow.removeClass("disabled");
		}
		else {
			applyTimeWindow.addClass("disabled");
		}
	});

	datetimeTo.on("dp.change", function (e) {

		if (e.date != null) {

			datetimeFrom.data("DateTimePicker").setMaxDate(e.date);
			applyTimeWindow.removeClass("disabled");
		}
		else {
			applyTimeWindow.addClass("disabled");
		}
	});

	removeDatetimeTo.on("click", function (e) {

		var disabled = datetimeTo.data("DateTimePicker").element.find('input').prop('disabled');

		if (!disabled) {

			datetimeTo.data("DateTimePicker").setDate(null);
			applyTimeWindow.addClass("disabled");
		}
	});

	applyTimeWindow.on("click", function (e) {

		applyCacheMonitorChange(period, segment, datetimeFrom, datetimeTo, applyTimeWindow);
	});

	initCacheMonitorMonitor(maxBytesLocalHeap, maxBytesLocalDisk,
			cacheHAHeapHistory, cacheHAHeap, 
			cacheHADiskHistory, cacheHADisk, 
			cacheHeapHistory, cacheHeap, 
			cacheDiskHistory, cacheDisk);

	// Asignación de la función de destrución de los procesos activos.
	App.destructionFunction = destroyCacheMonitor;

	var to = new Date();
	var from = new Date(to.getTime() - 600000);

	datetimeFrom.data("DateTimePicker").setDate(from);
	datetimeTo.data("DateTimePicker").setDate(to);

	datetimeFrom.data("DateTimePicker").disable();
	datetimeTo.data("DateTimePicker").disable();

	// Se aplica la ventana temporal inicial.
	applyCacheMonitorChange(period, segment, datetimeFrom, datetimeTo, applyTimeWindow);
}

/**
 * Función instanciada al cerrar la ventana de monitorización de caché.
 */
function destroyCacheMonitor() {

	stopCacheMonitor();

	cacheMonitorWindow = null;
}

