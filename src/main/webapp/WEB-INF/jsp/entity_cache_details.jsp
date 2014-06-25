<%@page import="java.util.Iterator"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCacheManager"%>
<%@page import="com.fav24.dataservices.domain.cache.EntityCache"%>
<%@page import="com.fav24.dataservices.domain.cache.CacheConfiguration.MemoryStoreEvictionPolicy"%>
<%@page import="com.fav24.dataservices.xml.cache.StorageSize"%>

<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<%!String bodyContent;%>
<%!String entity;%>
<%!String cacheManagerName;%>
<%
	StringBuilder output = new StringBuilder();
	entity = (String)request.getAttribute("entity");
	cacheManagerName = (String)request.getAttribute("cacheManager");
	
	try {
		EntityCacheManager entityCacheManager = (EntityCacheManager)request.getAttribute("cacheManagerConfiguration");
		EntityCache entityCache = (EntityCache)request.getAttribute("cacheConfiguration");

		if (entityCache == null || entityCacheManager == null) {
			
			output.append("No ha sido posible localizar la cach&eacute; <strong>").append(entity).append("</strong> en el gestor <strong>").
			append(cacheManagerName).append("</strong>.");
		} 
		else {
			output.append("<p>Detalle de la configuraci&oacute;n<p/>");

			// Caducidad.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Caducidad de los elementos</div>");
			output.append("<div class='panel-body'>");
			output.append("<ul class='list-group'>");

			output.append("<li class='list-group-item'>").append("<strong>Eternos: </strong> ").append(entityCache.getExpiry().isEternal() ? "S&iacute;" : "No").append("</li>");
			String liStyle;
			if (entityCache.getExpiry().isEternal()) {
				liStyle = "<li class='list-group-item' style='opacity:0.6;'>";
			}
			else {
				liStyle = "<li class='list-group-item'>";
			}

			output.append(liStyle).append("<strong>Tiempo m&aacute;ximo sin ser consultados en segundos: </strong> ").append(entityCache.getExpiry().getTimeToIdleSeconds()).append("</li>");
			output.append(liStyle).append("<strong>Tiempo m&aacute;ximo de vida en segundos: </strong> ").append(entityCache.getExpiry().getTimeToLiveSeconds()).append("</li>");
			output.append("<li class='list-group-item'>").append("<strong>Intervalo en segundos entre chequeos de caducidad de los elementos en disco: </strong> ").append(entityCache.getDiskExpiryThreadIntervalSeconds()).append("</li>");
			output.append("</ul>");
			output.append("</div>");
			output.append("</div>");

			// Límites de almacenamiento.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>L&iacute;mites de alacenamiento</div>");
			output.append("<div class='panel-body'>");
			output.append("<ul class='list-group'>");
			output.append("<li class='list-group-item'>").append("<strong>Memoria:</strong> ").append(StorageSize.fromBytesToString(entityCache.getMaxBytesLocalHeap())).append("</li>");
			output.append("<li class='list-group-item'");
			if (!entityCache.getPersistente().isActive()) {
				output.append(" style='opacity:0.6;'");
			}
			output.append(">").append("<strong>Disco:</strong> ").append(StorageSize.fromBytesToString(entityCache.getMaxBytesLocalDisk())).append("</li>");
			output.append("<li class='list-group-item'>").append("<strong>Persistencia:</strong> ").append(entityCache.getPersistente().isActive() ? "S&iacute;" : "No").append("</li>");
			output.append("</ul>");
			
			// Respecto al manager que contiene esta caché.
			output.append("<div class='row'>");
			
			output.append("<div class='col-sm-6'>");
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Memoria respecto al gestor</div>");
			output.append("<div class='panel-body'>");
			output.append("<div id='managerRelativeHeap' style='width:500px; height:250px;'></div>");
			output.append("</div>");
			output.append("</div>");
			output.append("</div>");

			output.append("<div class='col-sm-6'>");
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Disco respecto al gestor</div>");
			output.append("<div class='panel-body'>");
			output.append("<div id='managerRelativeDisk' style='width:500px; height:250px;'></div>");
			output.append("</div>");
			output.append("</div>");
			output.append("</div>");
			
			output.append("</div>");
			
			long heapPercent = (entityCache.getMaxBytesLocalHeap() * 100) / entityCacheManager.getMaxBytesLocalHeap();
			output.append("<script type='text/javascript'>");
			output.append('\n');
			output.append("var managerRelativeDataHeap = [");
			output.append(heapPercent);
			output.append(",").append(100-heapPercent);
			output.append("];");

			long diskPercent = (entityCache.getMaxBytesLocalDisk() * 100) / entityCacheManager.getMaxBytesLocalDisk();
			output.append('\n');
			output.append("var managerRelativeDataDisk = [");
			output.append(diskPercent);
			output.append(",").append(100-diskPercent);
			output.append("];");

			output.append("</script>");
			
			output.append("</div>");
			output.append("</div>");
			
			// Política de desalojo del heap.
			output.append("<div class='panel panel-info'>");
			output.append("<div class='panel-heading'>Pol&iacute;ca de desalojo de la memoria</div>");
			output.append("<div class='panel-body'>");
			output.append("<p>");
			
			if (MemoryStoreEvictionPolicy.LRU.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desaloja de memoria, el elemento que hace m&aacute;s tiempo que no se consulta. LRU (Least Recently Used)");
			}
			else if (MemoryStoreEvictionPolicy.LFU.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desaloja de memoria, el elemento que se usa menos frecuentemente. LFU (Least Frequently Used)");
			}
			else if (MemoryStoreEvictionPolicy.FIFO.equals(entityCache.getMemoryStoreEvictionPolicy())) {
				output.append("Se desalojan los elemento de memoria, en el mismo orden de entrada en la cach&eacute; de los elementos. FIFO (First In First Out)");
			}
			
			output.append("</p>");
			output.append("</div>");
			output.append("</div>");

		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}

	bodyContent = output.toString();
%>

<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jqplot/jquery.jqplot.min.css"/>"></link>
<script class="include" type="text/javascript" src="<c:url value="/resources/js/jqplot/jquery.jqplot.min.js"/>"></script>
<script class="include" type="text/javascript" src="<c:url value="/resources/js/jqplot/plugins/jqplot.pieRenderer.min.js"/>"></script>
<script class="include" type="text/javascript" src="<c:url value="/resources/js/jqplot/plugins/jqplot.enhancedLegendRenderer.min.js"/>"></script>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick="sendGetRequest('cache/cacheManagerConfiguration?cacheManager=${cacheManager.getName()}');" type="button" class="btn btn-default btn-sm pull-right">
   			<span class="glyphicon glyphicon-th-list"></span>
   		</button>
		<div class="panel-heading"><h3>${entity}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<%=bodyContent%>
		</div>
	</div>
</div>

<script type="text/javascript">

	var managerRelativeHeap = jQuery.jqplot ('managerRelativeHeap', [managerRelativeDataHeap], 
	    { 
		grid: {
			drawBorder: false,
			drawGridlines: false,
			background: '#ffffff',
			shadow: false
		},
		axesDefaults: {
             
        },
        animate: true,
        // Will animate plot on calls to plot1.replot({resetAxes:true})
        animateReplot: true,
        cursor: {
            show: true,
            zoom: true,
            looseZoom: true,
            showTooltip: false
        },
        seriesColors:['#F38630', '#69D2E7'],
        seriesDefaults: {
				trendline: {
					show: true
					},
				shadow: false,
				// Make this a pie chart.
				renderer: jQuery.jqplot.PieRenderer,
				rendererOptions: {
					// Put data labels on the pie slices.
					// Turn off filling of slices.
					fill: true,
					showDataLabels: true,
					// Add a margin to seperate the slices.
					sliceMargin: 4,
					// stroke the slices with a little thicker line.
					lineWidth: 5
				}
			},
		legend: {
				renderer: jQuery.jqplot.EnhancedLegendRenderer,
	            labels: ['${entity}', '${cacheManager}'],
				show: true,
				showLabels: true,
				showSwatches: false,
				rowSpacing: '10px',
				marginLeft: '-150px',
				placement: 'outsideGrid',
				location: 'e',
				border: 'none'
			}
	});

	
	var managerRelativeDisk = jQuery.jqplot ('managerRelativeDisk', [managerRelativeDataDisk], 
	    { 
		grid: {
			drawBorder: false,
			drawGridlines: false,
			background: '#ffffff',
			shadow: false
		},
		axesDefaults: {
             
        },
        animate: true,
        // Will animate plot on calls to plot1.replot({resetAxes:true})
        animateReplot: true,
        cursor: {
            show: true,
            zoom: true,
            looseZoom: true,
            showTooltip: false
        },
        seriesColors:['#F38630', '#69D2E7'],
        seriesDefaults: {
				trendline: {
					show: true
					},
				shadow: false,
				// Make this a pie chart.
				renderer: jQuery.jqplot.PieRenderer,
				rendererOptions: {
					// Put data labels on the pie slices.
					// Turn off filling of slices.
					fill: true,
					showDataLabels: true,
					// Add a margin to seperate the slices.
					sliceMargin: 4,
					// stroke the slices with a little thicker line.
					lineWidth: 5
				}
			},
		legend: {
				renderer: jQuery.jqplot.EnhancedLegendRenderer,
	            labels: ['${entity}', '${cacheManager}'],
				show: true,
				showLabels: true,
				showSwatches: false,
				rowSpacing: '10px',
				marginLeft: '-150px',
				placement: 'outsideGrid',
				location: 'e',
				border: 'none'
			}
		});
</script>