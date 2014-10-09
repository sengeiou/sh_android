/**
 * Estructura global de consulta al sistema y la ventana de tiempo de datos se desea obtener.
 */
SystemMonitorWindow = function() {

	/**
	 * Función de refresco de la ventana temporal de monitorización.
	 */
	updateSystenMonitorWindow = function(period, from, to) {

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
				timeRange: this.nextOffset == null ? this.timeRange : this.period
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

		updateSystenMonitorWindow: updateSystenMonitorWindow,
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
function applySystemMonitorChange(periodSelector, segmentSelector, datetimeFrom, datetimeTo, applyTimeWindow) {

	var period = parseInt(periodSelector.val());
	var segment = parseInt(segmentSelector.val());
	var from;
	var to;

	stopSystemMonitor();

	if (segment == -1) {

		from = new Date(datetimeFrom.data("DateTimePicker").getDate().unix() * 1000);
		to = new Date(datetimeTo.data("DateTimePicker").getDate().unix() * 1000);
	}
	else {

		to = new Date();
		from = new Date(to.getTime() - segment * 1000);
	}

	if (to != null && from != null) {

		systemMonitorWindow.updateSystenMonitorWindow(period, from, to);
		systemCpuMonitorRequestWindow(systemMonitorWindow, false);
		systemMemoryMonitorRequestWindow(systemMonitorWindow, false);

		if (segment != -1) {
			startSystemMonitor(systemMonitorWindow);
		}
	}

	applyTimeWindow.addClass("disabled");
}

/**
 * Función de inicialización del monitor del sistema.
 * 
 * @param periodSelector Nombre del elemento que contiene el periodo de refresco. 
 * @param segmentSelector Nombre del elemento que contiene el segmento a refrescar. 
 * @param datetimePickerAccessFrom Nombre del elemento que contiene el momento inicial del intérvalo a mostrar.
 * @param datetimePickerAccessTo Nombre del elemento que contiene el momento final del intérvalo a mostrar.
 * @param applyTimeWindowButton Nombre del botón que aplica los cambios de configuración de la ventana de tiempo.
 * 
 * @param cpuHistory Nombre del elemento en el que se renderiza el histórico de CPU.
 * @param cpuInstant Nombre del elemento en el que se renderiza el instante más reciente de CPU.
 * @param threadsHistory Nombre del elemento en el que se renderiza el histórico de hilos de ejecución.
 * @param threadsInstant Nombre del elemento en el que se renderiza el instante más reciente de hilos de ejecución.
 * @param memoryHistory Nombre del elemento en el que se renderiza el histórico de memoria.
 * @param memoryInstant Nombre del elemento en el que se renderiza el instante más reciente de memoria.
 */
function initSystemMonitor(periodSelector, segmentSelector, datetimePickerAccessFrom, datetimePickerAccessTo, applyTimeWindowButton,
		cpuHistory, cpuInstant,
		threadsHistory, threadsInstant,
		memoryHistory, memoryInstant)
{
	systemMonitorWindow = SystemMonitorWindow();

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

		applySystemMonitorChange(period, segment, datetimeFrom, datetimeTo, applyTimeWindow);
	});

	initSystemMonitorMonitor(cpuHistory, cpuInstant,
			threadsHistory, threadsInstant, memoryHistory, memoryInstant);

	// Asignación de la función de destrución de los procesos activos.
	App.destructionFunction = destroySystemMonitor;

	var to = new Date();
	var from = new Date(to.getTime() - 600000);

	datetimeFrom.data("DateTimePicker").setDate(from);
	datetimeTo.data("DateTimePicker").setDate(to);

	datetimeFrom.data("DateTimePicker").disable();
	datetimeTo.data("DateTimePicker").disable();

	// Se aplica la ventana temporal inicial.
	applySystemMonitorChange(period, segment, datetimeFrom, datetimeTo, applyTimeWindow);
}

/**
 * Función instanciada al cerrar la ventana de monitorización del sistema.
 */
function destroySystemMonitor() {

	stopSystemMonitor();

	systemMonitorWindow = null;
}

