<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script src="<c:url value="/resources/js/script.min.js"/>"></script>

<script type="text/javascript">

	var App = new Object();

	App.baseURL = "<c:url value=''/>";
	App.servicesURL = "<c:url value='/rest'/>";
	App.pagesURL = "<c:url value='/jsp'/>";
	App.imagesURL = "<c:url value='/resources/img'/>";
	App.cssURL = "<c:url value='/resources/css'/>";
	App.jsURL = "<c:url value='/resources/js'/>";
	
	// Asignación de la función de destrución de los procesos activos de una determinada página.
	App.destructionFunction = null;

</script>

<script data-id="App.Scripts">

    App.Scripts = {
        /* Primer bloque. */
        core: [
            '<c:url value="/resources/js/jquery.min.js"/>',
            '<c:url value="/resources/js/utils.js"/>',
            '<c:url value="/resources/js/sprintf.js"/>'
    	],
        /* Segundo bloque. */
        main: [
            '<c:url value="/resources/js/bootstrap.min.js"/>',
			'<c:url value="/resources/js/moment/moment-with-locales.min.js"/>',
			'<c:url value="/resources/js/chart/Chart.min.js"/>',
			'<c:url value="/resources/js/syntaxhighlighter/shCore.min.js"/>',
			'<c:url value="/resources/js/requests.js"/>',
            '<c:url value="/resources/js/main.js"/>'
        ],
        /* Tercer bloque. */
        plugins: [

            // Complementos de boostrap.
			'<c:url value="/resources/js/datetimepicker/bootstrap-datetimepicker.min.js"/>',
			'<c:url value="/resources/js/combobox/bootstrap-combobox.js"/>',

			// system_monitor.jsp dependency.
			'<c:url value="/resources/js/chart/plugins/Chart.StackedBar.js"/>',
			'<c:url value="/resources/js/chart/custom/chartHelper.js"/>',
			'<c:url value="/resources/js/chart/custom/chartDefaultConfig.js"/>',
			
			// system_context.jsp dependency.
			'<c:url value="/resources/js/custom/tree.js"/>',
			
			// hook_details.jsp dependency.
			'<c:url value="/resources/js/syntaxhighlighter/shAutoloader.min.js"/>',
			
			// Configuraciones de las graficas del monitorización. 
			'<c:url value="/resources/js/monitors/system_monitor_config.js"/>',
			'<c:url value="/resources/js/monitors/cache_monitor_config.js"/>'

        ],
        custom_L1: [

			//system_workload.jsp dependency.
			'<c:url value="/resources/js/system_workload.js"/>',
          
			// system_monitor.jsp dependency.
			//'<c:url value="/resources/js/monitors/memory_monitor.js"/>',
			'<c:url value="/resources/js/monitors/system_monitor.js"/>',
			// cache_monitor.jsp dependency.
			'<c:url value="/resources/js/monitors/cache_monitor.js"/>',
			// entity_cache_manager_details.jsp dependency.
			'<c:url value="/resources/js/entity_cache_manager_details.js"/>',
			// entity_cache_details.jsp dependency.
			'<c:url value="/resources/js/entity_cache_details.js"/>'
        ],
        custom_L2: [
			'<c:url value="/resources/js/system_monitor.js"/>',
			'<c:url value="/resources/js/cache_monitor.js"/>'
        ]
    };

    $script(App.Scripts.core, 'core');

    $script.ready([ 'core' ], function () {
        $script(App.Scripts.main, 'main');
    });
    
    $script.ready([ 'core', 'main' ], function () {
        $script(App.Scripts.plugins, 'plugins');
    });
    
    $script.ready([ 'core', 'main', 'plugins' ], function () {
        $script(App.Scripts.custom_L1, 'custom_L1');
    });
    
    $script.ready([ 'core', 'main', 'plugins', 'custom_L1' ], function () {
        $script(App.Scripts.custom_L2, 'custom_L2');
    });
    
</script>
