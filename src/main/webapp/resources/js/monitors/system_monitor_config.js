//Histórico carga de CPU.
var cpuHistoryChartOptions = clone(lineChartOptions);

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

//Acumulado de accesos a disco.
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

