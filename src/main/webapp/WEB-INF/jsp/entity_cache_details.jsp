<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="customFunctions.tld" prefix="cf"%>


<!-- Panel de detalle de una cierta entidad publicada. -->
<div id="entityCacheDetails">

	<!-- Detalles de la entidad -->
	<div class="panel panel-info">
		<button onClick="sendGetRequest('cache/cacheManagerConfiguration?cacheManager=${cacheManager}');" type="button" class="btn btn-default btn-sm pull-right">
   			<span class="glyphicon glyphicon-th-list"></span>
   		</button>
		<div class="panel-heading"><h3>${cache}</h3></div>
		<!-- Lista de entidades -->
		<div class="panel-body">
			<c:choose>
				<c:when test="${empty cache or empty cacheManager}">
					<p>No ha sido posible localizar la cach&eacute; <strong>${cache}</strong> en el gestor <strong>${cacheManager}</strong>.</p>
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
						
							<!-- Tamaños respecto al manager que contiene esta caché. -->
							<div class="row-fluid">
								<div class="col-sx-6 col-md-6">
									<div id="cacheHeap">
										<canvas style="width: 100%; height: 150px;" width="" height="150px"></canvas>
										<div class="chart-legend"></div>
									</div>
								</div>
								<div class="col-sx-6 col-md-6">
									<div id="cacheDisk" >
										<canvas style="width: 100%; height: 150px;" width="" height="150px"></canvas>
	                                    <div class="chart-legend"></div>
									</div>
								</div>
							</div>
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
drawCacheSizes('${cacheManager}', '${cache}',
		'cacheHeap', ${cacheConfiguration.getMaxBytesLocalHeap()}, ${cacheManagerConfiguration.getMaxBytesLocalHeap()},
		'cacheDisk', ${cacheConfiguration.getMaxBytesLocalDisk()}, ${cacheManagerConfiguration.getMaxBytesLocalDisk()}
);
</script>