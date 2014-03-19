/**
 * jqPlot Custom tick renderer.
 * 
 * Este renderer se usa para la representación de tamaños de almacenamiento.
 * 
 * 
 * 
 */
$.jqplot.BytesTickFormatter = function (format, val) {
	
	var KILO = 1024;
	var MEGA = 1000 * KILO;
	var GIGA = 1000 * MEGA;
	
    if (typeof val == 'number') {
    	
    	if (!val) {

    		return "";
    	}

    	if (val < KILO) {
    		return $.jqplot.sprintf("%d", val);
    	}
    	else {

    		if (val < MEGA) {
   				val = val / KILO;
   				return $.jqplot.sprintf("%.2fK", val);
    		}
    		else if (val < GIGA) {
   				val = val / MEGA;
   				return $.jqplot.sprintf("%.2fM", val);
    		}
    		else {
    			val = val / GIGA;
   				return $.jqplot.sprintf("%.2fG", val);
    		}
    	}        
    }
    else {
        return String(val);
    }
};
