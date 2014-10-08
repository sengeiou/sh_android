//Opciones globales por defecto de los gráficos de líneas.
var lineChartOptions = {

		// Boolean - whether or not the chart should be responsive and resize when the browser does.
		responsive : true,

		// Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
		maintainAspectRatio: false,

		//Boolean - If we want to override with a hard coded scale
		scaleOverride : false,

		//** Required if scaleOverride is true **
		//Number - The number of steps in a hard coded scale
		scaleSteps : null,
		//Number - The value jump in the hard coded scale
		scaleStepWidth : null,
		//Number - The scale starting value
		scaleStartValue : null,

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
		pointHitDetectionRadius : 4,

		//Number - The percentage of the chart that we cut out of the middle
		percentageInnerCutout : 50, // This is 0 for Pie charts

		//Number - Amount of animation steps
		animationSteps : 50,

		// Boolean - If we should show the scale at all
		showScale : true, 

		//Boolean - Whether to show labels on the scale	
		scaleShowLabels: true,

		// Boolean - Whether the scale should start at zero, or an order of magnitude down from the lowest value
		scaleBeginAtZero: true,

		//Interpolated JS string - can access value
		scaleLabel : "<%= value%>",

		//Number - Scale label font size in pixels	
		scaleFontSize : 12,

		// String - Template string for single tooltips
		tooltipTemplate: "<%if (label){%><%=label%>: <%}%><%= value%>",

		// String - Template string for single tooltips
		multiTooltipTemplate: "<%= value %>",

		//String - Animation easing effect
		animationEasing : "easeOutBounce",

		//Boolean - Whether we animate the rotation of the Doughnut
		animateRotate : true,

		//Boolean - Whether we animate scaling the Doughnut from the centre
		animateScale : true,

		/** Atributos custom **/
		//Booelan - Muestra u oculta las etiquetas del eje x de la gráfica.
		showXLabels : false 
};

//Opciones globales por defecto de los gráficos de sectores circulares tipo donut.
var doughnutChartOptions = {

		// Boolean - whether or not the chart should be responsive and resize when the browser does.
		responsive : true,

		// Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
		maintainAspectRatio: false,

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

//Opciones globales por defecto de los gráficos kiviat (Radar).
var radarChartOptions = {

		// Boolean - whether or not the chart should be responsive and resize when the browser does.
		responsive : true,

		// Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
		maintainAspectRatio: false,

		//Boolean - Whether to show lines for each scale point
		scaleShowLine : true,

		//Boolean - Whether we show the angle lines out of the radar
		angleShowLineOut : true,

		//Boolean - Whether to show labels on the scale
		scaleShowLabels : false,

		// Boolean - Whether the scale should begin at zero
		scaleBeginAtZero : true,

		//String - Colour of the angle line
		angleLineColor : "rgba(0,0,0,.1)",

		//Number - Pixel width of the angle line
		angleLineWidth : 1,

		//String - Point label font declaration
		pointLabelFontFamily : "Arial",

		//String - Point label font weight
		pointLabelFontStyle : "normal",

		//Number - Point label font size in pixels
		pointLabelFontSize : 10,

		//String - Point label font colour
		pointLabelFontColor : "#666",

		//Boolean - Whether to show a dot for each point
		pointDot : true,

		//Number - Radius of each point dot in pixels
		pointDotRadius : 3,

		//Number - Pixel width of point dot stroke
		pointDotStrokeWidth : 1,

		//Number - amount extra to add to the radius to cater for hit detection outside the drawn point
		pointHitDetectionRadius : 20,

		//Boolean - Whether to show a stroke for datasets
		datasetStroke : true,

		//Number - Pixel width of dataset stroke
		datasetStrokeWidth : 2,

		//Boolean - Whether to fill the dataset with a colour
		datasetFill : true,

		//String - A legend template
		legendTemplate : "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].strokeColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
};

var stackedBarChartOptions = {

		// Boolean - whether or not the chart should be responsive and resize when the browser does.
		responsive : true,

		// Boolean - whether to maintain the starting aspect ratio or not when responsive, if set to false, will take up entire container
		maintainAspectRatio: false,

		//Boolean - Whether the scale should start at zero, or an order of magnitude down from the lowest value
		scaleBeginAtZero : true,

		//Boolean - Whether grid lines are shown across the chart
		scaleShowGridLines : true,

		//String - Colour of the grid lines
		scaleGridLineColor : "rgba(0,0,0,.1)",

		//Number - Width of the grid lines
		scaleGridLineWidth : 1,

		//Boolean - If there is a stroke on each bar
		barShowStroke : true,

		//Number - Pixel width of the bar stroke
		barStrokeWidth : 2,

		//Number - Spacing between each of the X value sets
		barValueSpacing : 5,

		//Number - Spacing between data sets within X values
		barDatasetSpacing : 1,
		
		//String - A legend template
		legendTemplate : "<ul class=\"<%=name.toLowerCase()%>-legend\"><% for (var i=0; i<datasets.length; i++){%><li><span style=\"background-color:<%=datasets[i].lineColor%>\"></span><%if(datasets[i].label){%><%=datasets[i].label%><%}%></li><%}%></ul>"
};