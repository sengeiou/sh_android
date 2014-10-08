//Histórico de accesos a memoria.
var cacheHAHeapHistoryChartOptions = clone(lineChartOptions);

var cacheHAHeapHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Hits",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           },
		           {
		        	   label: "Adds",
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

//Acumulado de accesos a memoria.
var cacheHAHeapChartOptions = clone(doughnutChartOptions);

var cacheHAHeapChartData = [
                            {
                            	value: 10,
                            	color:"rgba(220,220,220,1)",
                            	highlight: "rgba(220,220,220,0.2)",
                            	label: "Hits"
                            },
                            {
                            	value: 50,
                            	color: "rgba(151,187,205,1)",
                            	highlight: "rgba(151,187,205,0.2)",
                            	label: "Adds"
                            }
                            ];

//Histórico de accesos a disco.
var cacheHADiskHistoryChartOptions = clone(lineChartOptions);

var cacheHADiskHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Hits",
		        	   fillColor: "rgba(220,220,220,0.2)",
		        	   strokeColor: "rgba(220,220,220,1)",
		        	   pointColor: "rgba(220,220,220,1)",
		        	   pointStrokeColor: "#fff",
		        	   pointHighlightFill: "#fff",
		        	   pointHighlightStroke: "rgba(220,220,220,1)",
		        	   data: []
		           },
		           {
		        	   label: "Adds",
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

//Acumulado de accesos a disco.
var cacheHADiskChartOptions = clone(doughnutChartOptions);

var cacheHADiskChartData = [
                            {
                            	value: 10,
                            	color:"rgba(220,220,220,1)",
                            	highlight: "rgba(220,220,220,0.2)",
                            	label: "Hits"
                            },
                            {
                            	value: 50,
                            	color: "rgba(151,187,205,1)",
                            	highlight: "rgba(151,187,205,0.2)",
                            	label: "Adds"
                            }
                            ];


//Histórico de uso de memoria.
var cacheHeapHistoryChartOptions = clone(lineChartOptions);
cacheHeapHistoryChartOptions.scaleLabel = "<%= bytesFormatter(Number(value))%>";
cacheHeapHistoryChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheHeapHistoryChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cacheHeapHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Memoria en uso",
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

//Media de uso de memoria.
var cacheHeapChartOptions = clone(doughnutChartOptions);
cacheHeapChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheHeapChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cacheHeapChartData = [
                          {
                        	  value: 10,
                        	  color:"rgba(220,220,220,1)",
                        	  highlight: "rgba(220,220,220,0.2)",
                        	  label: "En uso"
                          },
                          {
                        	  value: 50,
                        	  color: "rgba(151,187,205,1)",
                        	  highlight: "rgba(151,187,205,0.2)",
                        	  label: "Disponible"
                          }
                          ];


//Histórico de uso de disco.
var cacheDiskHistoryChartOptions = clone(lineChartOptions);
cacheDiskHistoryChartOptions.scaleLabel = "<%= bytesFormatter(Number(value)) %>";
cacheDiskHistoryChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheDiskHistoryChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";

var cacheDiskHistoryChartData = {
		labels: [],
		datasets: [
		           {
		        	   label: "Disco en uso",
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

//Media de uso de disco.
var cacheDiskChartOptions = clone(doughnutChartOptions);
cacheDiskChartOptions.tooltipTemplate = "<%if (label){%><%=label%>: <%}%><%= bytesFormatter(Number(value)) %>";
cacheDiskChartOptions.multiTooltipTemplate = "<%= bytesFormatter(Number(value)) %>";
	
var cacheDiskChartData = [
                          {
                        	  value: 10,
                        	  color:"rgba(220,220,220,1)",
                        	  highlight: "rgba(220,220,220,0.2)",
                        	  label: "En uso"
                          },
                          {
                        	  value: 50,
                        	  color: "rgba(151,187,205,1)",
                        	  highlight: "rgba(151,187,205,0.2)",
                        	  label: "Disponible"
                          }
                          ];
