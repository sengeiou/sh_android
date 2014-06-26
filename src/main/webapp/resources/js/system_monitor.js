
/**
 * Función de inicialización del monitor del sistema.
 * 
 * @param period Periodo de refresco en segundos. 
 * @param timeRange Rango temporal a mostrar.
 * 
 * @param cpuHistoryPlotElement Nombre del elemento en el que se renderiza el histórico de actividad de la cpu.
 * @param cpuLoadInstantPlotElement Nombre del elemento en el que se renderiza la carga instantanea de la cpu.
 * @param memoryHistoryPlotElement Nombre del elemento en el que se renderiza el histórico de distribución de la memoria.
 * @param committedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria reservada instantanea.
 * @param usedMemoryInstantPlotElement Nombre del elemento en el que se renderiza la memoria usada instantanea.
 */
function initSystemMonitor(period, timeRange,
		memoryHistoryPlotElement, committedMemoryInstantPlotElement, usedMemoryInstantPlotElement,
		cpuHistoryPlotElement, cpuLoadInstantPlotElement) {

	initMemoryMonitor(period, timeRange, memoryHistoryPlotElement, committedMemoryInstantPlotElement, usedMemoryInstantPlotElement);
	initCPUMonitor(period, timeRange, cpuHistoryPlotElement, cpuLoadInstantPlotElement);

	// Asignación de la función de destrución de los procesos activos.
	App.destructionFunction = destroySystemMonitor;
}

/**
 * Función instanciada al cerrar la ventana de monitorización del sistema.
 */
function destroySystemMonitor() {

	stopMemoryHistoryMonitor();
	startMemoryInstantMonitor();
	stopCPUHistoryMonitor();
	stopCPUInstantMonitor();

	delete MemoryMonitor;
	delete CPUMonitor;
}
