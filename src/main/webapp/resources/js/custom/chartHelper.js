/**
 * Estructura de una gráfica.
 */
function ChartHelper(chartId) {

	var chartContainer = document.getElementById(chartId);
	var canvas = chartContainer.getElementsByTagName("canvas")[0];
	var canvasContext = canvas.getContext("2d");
	var legendContainer;

	var elements = chartContainer.getElementsByTagName("div");
	for (var i = 0; i < elements.length; i++) { 
		if (elements[i].getAttribute("class") == "chart-legend") {
			legendContainer = elements[i];
			break;
		}
	}

	var chartType;

	var createChart = function(chartType, options, data)
	{
		this.chartType = chartType;

		if (this.chart != null) {
			this.chart.destroy();
		}

		if (this.chartType == "line") {
			this.chart = new Chart(this.canvasContext).Line(data, options);
			
			if (!options.showXLabels) {
				this.chart.scale.xLabels = new Array(this.chart.scale.xLabels.length);
				
				for (var i=0; i<this.chart.scale.xLabels.length; i++) {
					this.chart.scale.xLabels[i] = "";
				}
				this.chart.update();
			}
		}
		else if (this.chartType == "pie") { 
			this.chart = new Chart(this.canvasContext).Pie(data, options);
		}

		this.legendContainer.innerHTML = this.chart.generateLegend();
	}

	/**
	 * Añade un dato a la gráfica.
	 * 
	 * @param label Etiqueda del dato.
	 * @param data Valor o valores del dato.
	 * @param removeFirst True o false en función de si se desea eliminar el primer dato de todas las series.
	 * 
	 * @return En caso de que removeFirst sea true, retorna los datos que contenía el primer punto.
	 */
	var setData = function(label, data, removeFirst) {

		if (this.chartType == "line") {
			
			if (this.chart.options.showXLabels) {
				this.chart.scale.xLabels.push(label);
			}
			else {
				this.chart.scale.xLabels.push("");
			}

			if (removeFirst != undefined && removeFirst) {
				this.chart.scale.xLabels.shift();
			}

			var firstData = new Array(data.length);

			for (var i=0; i<data.length; i++) {

				var points = this.chart.datasets[i].points;

				if (points != null) {

					var point;

					if (removeFirst != undefined && removeFirst) {

						point = points[0];
						firstData[i] = point.value;

						points.push(point);
						points.shift();

					}
					else {

						point = clone(points[0]);
						points.push(point);
					}

					point.value = data[i];
				}
			}

			this.chart.update();

			return firstData;
		}
		else if (this.chartType == "pie") {

			var previousData = new Array(data.length);

			for (var i=0; i<data.length; i++) {

				previousData[i] = this.chart.segments[i].value; 
				this.chart.segments[i].value = data[i];
			}

			this.chart.update();

			return previousData;
		}

		return null;
	}

	return {
		canvas : canvas,
		canvasContext : canvasContext,
		legendContainer : legendContainer,
		chart : null,
		chartType : chartType,
		createChart : createChart,
		setData : setData
	};
}
