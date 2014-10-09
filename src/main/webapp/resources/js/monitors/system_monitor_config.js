//Histórico carga de CPU.
var cpuHistoryChartOptions = clone(lineChartOptions);
cpuHistoryChartOptions.scaleLabel = "<%= Math.floor(Number(value)).toFixed(2).toLocaleString() + '%'%>";
cpuHistoryChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= Math.floor(Number(value)).toFixed(2).toLocaleString() + '%' %>";
cpuHistoryChartOptions.multiTooltipTemplate = "<%= Math.floor(Number(value)).toFixed(2).toLocaleString() + '%' %>";

var cpuHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Total",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           },
		           {
		        	   label: "Sistema",
		        	   fillColor: "rgba(251,187,205,0.2)",
		        	   strokeColor: "rgba(251,187,205,1)",
		        	   pointColor: "rgba(251,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(251,187,205,1)",
		        	   data: []
		           },
		           {
		        	   label: "Aplicación",
		        	   fillColor: "rgba(151,187,205,0.2)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,187,205,1)",
		        	   data: []
		           }
		           ]
};

//Carga de CPU en el instante más reciente.
var cpuInstantChartOptions = clone(doughnutChartOptions);

var cpuInstantChartData = [
                           {
                        	   value: 0,
                        	   color:"rgba(220,220,220,0.5)",
                        	   highlight: "rgba(220,220,220,0.1)",
                        	   label: "Inactividad"
                           },
                           {
                        	   value: 0,
                        	   color:"rgba(251,187,205,1)",
                        	   highlight: "rgba(251,187,205,0.2)",
                        	   label: "Sistema"
                           },
                           {
                        	   value: 0,
                        	   color: "rgba(151,187,205,1)",
                        	   highlight: "rgba(151,187,205,0.2)",
                        	   label: "Aplicación"
                           }
                           ];

//Histórico de hilos de ejecución.
var threadsHistoryChartOptions = clone(lineChartOptions);

var threadsHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Pico máximo",
		        	   fillColor: "rgba(251,187,205,0)",
		        	   strokeColor: "rgba(251,187,205,1)",
		        	   pointColor: "rgba(251,187,205,0)",
		        	   pointStrokeColor: "rgba(251,187,205,0)",
		        	   pointHighlightFill: "rgba(251,187,205,0)",
		        	   pointHighlightStroke: "rgba(251,187,205,0)",
		        	   data: []
		           },
		           {
		        	   label: "Hilos activos",
		        	   fillColor: "rgba(151,187,205,0.2)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,187,205,1)",
		        	   data: []
		           },
		           {
		        	   label: "Demonios",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           }
		           ]
};

//Taxonomía de los hilos de ejecución activos.
var threadsInstantChartOptions = clone(doughnutChartOptions);

var threadsInstantChartData = [
                               {
                            	   value: 0,
                            	   color: "rgba(151,187,205,1)",
                            	   highlight: "rgba(151,187,205,0.2)",
                            	   label: "Hilos"
                               },
                               {
                            	   value: 0,
                            	   color:"rgba(220,220,220,1)",
                            	   highlight: "rgba(220,220,220,0.2)",
                            	   label: "Demonios"
                               }
                               ];

//Histórico de uso de memoria.
var memoryHistoryChartOptions = clone(lineChartOptions);
memoryHistoryChartOptions.scaleLabel = "<%= bytesFormatter(Number(value))%>";
memoryHistoryChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
memoryHistoryChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var memoryHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Máximo",
		        	   fillColor: "rgba(251,187,205,0)",
		        	   strokeColor: "rgba(251,187,205,1)",
		        	   pointColor: "rgba(251,187,205,0)",
		        	   pointStrokeColor: "rgba(251,187,205,0)",
		        	   pointHighlightFill: "rgba(251,187,205,0)",
		        	   pointHighlightStroke: "rgba(251,187,205,0)",
		        	   data: []
		           },
		           {
		        	   label: "Inicial",
		        	   fillColor: "rgba(151,187,205,0)",
		        	   strokeColor: "rgba(151,187,205,1)",
		        	   pointColor: "rgba(151,187,205,0)",
		        	   pointStrokeColor: "rgba(151,187,205,0)",
		        	   pointHighlightFill: "rgba(151,187,205,0)",
		        	   pointHighlightStroke: "rgba(151,187,205,0)",
		        	   data: []
		           },
		           {
		        	   label: "Reservada",
		        	   fillColor: "rgba(151,287,205,0.2)",
		        	   strokeColor: "rgba(151,287,205,1)",
		        	   pointColor: "rgba(151,287,205,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(151,287,205,1)",
		        	   data: []
		           },
		           {
		        	   label: "Total usada",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           },
		           {
		        	   label: "Heap",
		        	   fillColor: "rgba(255,109,58,0)",
		        	   strokeColor: "rgba(255,109,58,1)",
		        	   pointColor: "rgba(255,109,58,0)",
		        	   pointStrokeColor: "rgba(255,109,58,0)",
		        	   pointHighlightFill: "rgba(255,109,58,0)",
		        	   pointHighlightStroke: "rgba(255,109,58,0)",
		        	   data: []
		           },
		           {
		        	   label: "Non Heap",
		        	   fillColor: "rgba(251,287,205,0)",
		        	   strokeColor: "rgba(251,287,205,1)",
		        	   pointColor: "rgba(251,287,205,0)",
		        	   pointStrokeColor: "rgba(251,287,205,0)",
		        	   pointHighlightFill: "rgba(251,287,205,0)",
		        	   pointHighlightStroke: "rgba(251,287,205,0)",
		        	   data: []
		           }
		           ]
};
