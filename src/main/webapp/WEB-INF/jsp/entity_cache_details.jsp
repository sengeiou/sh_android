<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="customFunctions.tld" prefix="cf"%>


<link class="include" rel="stylesheet" type="text/css" href="<c:url value="/resources/css/jqplot/jquery.jqplot.min.css"/>"></link>

<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick="sendGetRequest('cache/cacheManagerConfiguration?cacheManager=${cacheManager}');" type="button" class="btn btn-default btn-sm pull-right">
   			<span class="glyphicon glyphicon-th-list"></span>
   		</button>
		<div class="panel-heading"><h3>${entity}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty entity or empty cacheManager}">
					<p>No ha sido posible localizar la cach&eacute; <strong>${entity}</strong> en el gestor <strong>${cacheManager}</strong>.</p>
				</c:when>
				<c:otherwise>
					<p>Detalle de la configuraci&oacute;n<p/>
		
					<!-- Caducidad. -->
					<div class="panel panel-info">
						<div class="panel-heading">Caducidad de los elementos</div>
						<div class="panel-body">
							<br/>
							<ul class="list-group">
								<c:set var="isEternal" value="No"/>
								<c:if test="${cacheConfiguration.getExpiry().isEternal()}">
									<c:set var="persistentceStyle" value="opacity:0.6;"/>
									<c:set var="isEternal" value="S&iacute;"/>
								</c:if>
								<li class="list-group-item"><strong>Eternos: </strong> ${isEternal}</li>
								<li class="list-group-item" style="${persistentceStyle}"><strong>Tiempo m&aacute;ximo sin ser consultados en segundos: </strong> ${cacheConfiguration.getExpiry().getTimeToIdleSeconds()}</li>
								<li class="list-group-item"><strong>Intervalo en segundos entre chequeos de caducidad de los elementos en disco: </strong> ${cacheConfiguration.getDiskExpiryThreadIntervalSeconds()}</li>
							</ul>
						</div>
					</div>
		
					<!-- Límites de almacenamiento. -->
					<div class="panel panel-info">
						<div class="panel-heading">L&iacute;mites de alacenamiento</div>
						<div class="panel-body">
							<br/>
							<ul class="list-group">
								<li class="list-group-item"><strong>Memoria:</strong> ${cf:fromBytesToString(cacheConfiguration.getMaxBytesLocalHeap())}</li>
								<c:if test="${not cacheConfiguration.getPersistente().isActive()}">
									<c:set var="persistentceStyle" value="opacity:0.6;"/>
								</c:if>
								<li class="list-group-item" style="${persistentceStyle}"><strong>Disco:</strong> ${cf:fromBytesToString(cacheConfiguration.getMaxBytesLocalDisk())}</li>
								<li class="list-group-item"><strong>Persistencia:</strong> S&iacute;</li>
							</ul>
						
							<!-- Respecto al manager que contiene esta caché. -->
							<div class="row">
							
								<div class="col-sm-6">
									<div class="panel panel-info">
										<div class="panel-heading">Memoria respecto al gestor</div>
										<div class="panel-body">
											<div id="managerRelativeHeap" style="width:500px; height:250px;"></div>
										</div>
									</div>
								</div>
				
								<div class="col-sm-6">
									<div class="panel panel-info">
										<div class="panel-heading">Disco respecto al gestor</div>
										<div class="panel-body">
											<div id="managerRelativeDisk" style="width:500px; height:250px;"></div>
										</div>
									</div>
								</div>
									
							</div>

							<c:set var="heapPercent" value="${cacheConfiguration.getMaxBytesLocalHeap() / cacheManagerConfiguration.getMaxBytesLocalHeap()}"/>
							<c:set var="heapPercent" value="${heapPercent * 100}"/>
							<c:set var="diskPercent" value="${cacheConfiguration.getMaxBytesLocalDisk() / cacheManagerConfiguration.getMaxBytesLocalDisk()}"/>
							<c:set var="diskPercent" value="${diskPercent * 100}"/>

							<script type="text/javascript">
								var managerRelativeDataHeap = [${heapPercent}, 100-${heapPercent}];
								var managerRelativeDataDisk = [${diskPercent}, 100-${diskPercent}];
							</script>
						</div>
					</div>
					
					<!-- Política de desalojo del heap. -->
					<div class="panel panel-info">
						<div class="panel-heading">Pol&iacute;ca de desalojo de la memoria</div>
						<div class="panel-body">
							<p>
								<c:choose>
									<c:when test="${cacheConfiguration.getMemoryStoreEvictionPolicy() == 'LRU'}">
										Se desaloja de memoria, el elemento que hace m&aacute;s tiempo que no se consulta. LRU (Least Recently Used)
									</c:when>
									<c:when test="${cacheConfiguration.getMemoryStoreEvictionPolicy() == 'LFU'}">
										Se desaloja de memoria, el elemento que se usa menos frecuentemente. LFU (Least Frequently Used)
									</c:when>
									<c:when test="${cacheConfiguration.getMemoryStoreEvictionPolicy() == 'FIFO'}">
										Se desalojan los elementos de memoria, en el mismo orden de entrada en la cach&eacute; de los elementos. FIFO (First In First Out)
									</c:when>
								</c:choose>
							</p>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
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