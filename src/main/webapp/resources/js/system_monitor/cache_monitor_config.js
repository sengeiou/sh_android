// Histórico de accesos a memoria.
var cacheHAHeapHistoryChartOptions = {

		responsive : true,

		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,

		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",

		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,

		 //Number - Radius of each point dot in pixels
	    pointDotRadius : 3,

	    //Number - Pixel width of point dot stroke
	    pointDotStrokeWidth : 1,

	    //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
	    pointHitDetectionRadius : 6,
	    
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		scaleShowLabels: false,
		
		//Interpolated JS string - can access value
		scaleLabel : "<%=value%>",
		
		//Number - Scale label font size in pixels	
		scaleFontSize : 0,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
var cacheHAHeapChartOptions = {

		responsive : true,

		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,

		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",

		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,

		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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

// Histórico de accesos a disco.
var cacheHADiskHistoryChartOptions = {
		
		responsive : true,

		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,

		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",

		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,

		 //Number - Radius of each point dot in pixels
	    pointDotRadius : 3,

	    //Number - Pixel width of point dot stroke
	    pointDotStrokeWidth : 1,

	    //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
	    pointHitDetectionRadius : 6,
	    
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		scaleShowLabels: false,
		
		//Interpolated JS string - can access value
		scaleLabel : "<%=value%>",
		
		//Number - Scale label font size in pixels	
		scaleFontSize : 0,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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

//Acumulado de accesos a memoria.
var cacheHADiskChartOptions = {
		
		responsive : true,
		
		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,
		
		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",
		
		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,
		
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts
		
		//Number - Amount of animation steps
		animationSteps : 50,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",
		
		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,
		
		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
var cacheHeapHistoryChartOptions = {

		responsive : true,

		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,

		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",

		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,

		 //Number - Radius of each point dot in pixels
	    pointDotRadius : 3,

	    //Number - Pixel width of point dot stroke
	    pointDotStrokeWidth : 1,

	    //Number - amount extra to add to the radius to cater for hit detection outside the drawn point
	    pointHitDetectionRadius : 6,
	    
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		scaleShowLabels: false,
		
		//Interpolated JS string - can access value
		scaleLabel : "<%=value%>",
		
		//Number - Scale label font size in pixels	
		scaleFontSize : 0,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
var cacheHeapChartOptions = {

		responsive : true,

		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,

		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",

		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,

		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
var cacheDiskHistoryChartOptions = {
		
		responsive : true,
		
		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,
		
		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",
		
		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,
		
		//Number - Radius of each point dot in pixels
		pointDotRadius : 3,
		
		//Number - Pixel width of point dot stroke
		pointDotStrokeWidth : 1,
		
		//Number - amount extra to add to the radius to cater for hit detection outside the drawn point
		pointHitDetectionRadius : 6,
		
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts
		
		//Number - Amount of animation steps
		animationSteps : 50,
		
		scaleShowLabels: false,
		
		//Interpolated JS string - can access value
		scaleLabel : "<%=value%>",
		
		//Number - Scale label font size in pixels	
		scaleFontSize : 0,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",
		
		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,
		
		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
var cacheDiskChartOptions = {
		
		responsive : true,
		
		//Boolean - Whether we should show a stroke on each segment
		segmentShowStroke : true,
		
		//String - The colour of each segment stroke
		segmentStrokeColor : "#fff",
		
		//Number - The width of each segment stroke
		segmentStrokeWidth : 2,
		
		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts
		
		//Number - Amount of animation steps
		animationSteps : 50,
		
		//String - Animation easing effect
		animationEasing : "easeOutBounce",
		
		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,
		
		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true
};

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
