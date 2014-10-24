/**
 * Función de clonado de objetos en profundidad.
 * 
 * @param obj Objeto a clonar.
 *  
 * @returns clon.
 */
function clone(obj) {
	
    var clonedObjectsArray = [];
    var originalObjectsArray = []; //used to remove the unique ids when finished
    var next_objid = 0;

    function objectId(obj) {
        if (obj == null) return null;
        if (obj.__obj_id == undefined){
            obj.__obj_id = next_objid++;
            originalObjectsArray[obj.__obj_id] = obj;
        }
        return obj.__obj_id;
    }

    function cloneRecursive(obj) {
        if (null == obj || typeof obj == "string" || typeof obj == "number" || typeof obj == "boolean") return obj;

        // Handle Date
        if (obj instanceof Date) {
            var copy = new Date();
            copy.setTime(obj.getTime());
            return copy;
        }

        // Handle Array
        if (obj instanceof Array) {
            var copy = [];
            for (var i = 0; i < obj.length; ++i) {
                copy[i] = cloneRecursive(obj[i]);
            }
            return copy;
        }

        // Handle Object
        if (obj instanceof Object) {
            if (clonedObjectsArray[objectId(obj)] != undefined)
                return clonedObjectsArray[objectId(obj)];

            var copy;
            if (obj instanceof Function)//Handle Function
                copy = function(){return obj.apply(this, arguments);};
            else
                copy = {};

            clonedObjectsArray[objectId(obj)] = copy;

            for (var attr in obj)
                if (attr != "__obj_id" && obj.hasOwnProperty(attr))
                    copy[attr] = cloneRecursive(obj[attr]);                 

            return copy;
        }       


        throw new Error("Unable to copy obj! Its type isn't supported.");
    }
    var cloneObj = cloneRecursive(obj);



    //remove the unique ids
    for (var i = 0; i < originalObjectsArray.length; i++)
    {
        delete originalObjectsArray[i].__obj_id;
    };

    return cloneObj;
}

/**
 * Retorna el valor de tamaño en bytes formateado, con el fin de facilitar sulectura.
 * 
 * @param value Valor a formatear.
 */
var KILO = 1024;
var MEGA = 1000 * KILO;
var GIGA = 1000 * MEGA;
function bytesFormatter(value) {
	
    if (typeof value == 'number') {
    	
    	if (!value) {

    		return "";
    	}

    	if (value < KILO) {
    		return String.sprintf("%d", value);
    	}
    	else {

    		if (value < MEGA) {
    			value = value / KILO;
   				return String.sprintf("%.2fK", value);
    		}
    		else if (value < GIGA) {
    			value = value / MEGA;
   				return String.sprintf("%.2fM", value);
    		}
    		else {
    			value = value / GIGA;
   				return String.sprintf("%.2fG", value);
    		}
    	}        
    }
    else {
        return String(value);
    }
}

/**
 * Función que retorna el contenido del objeto indicado, 
 * en forma de cadena de texto.
 * 
 * @param o Objeto a representar en forma de cadena de texto.
 * 
 * @return el contenido del objeto indicado, en forma de cadena de texto.
 */
function printObject(o) {
	var out = '';
	for (var p in o) {
		out += p + ': ' + o[p] + '\n';
	}

	return out;
}

/**
 * Retorna la cadena de texto indicada, con los caracteres escapados mediante entidades HTML.
 * 
 * @param str Cadena de texto a escapar.
 * 
 * @return la cadena de texto indicada, con los caracteres escapados mediante entidades HTML.
 */
function escapeHtml(str) {

	if (typeof(str) == "string") {

		if (typeof jQuery !== 'undefined') {

			/*
			 * Si se dispone de jQuery, se crea un div anónimo,
			 * se le asigna un texto de contenido y,
			 * se retorna el texto contenido en HTML escapado.
			 */

			var dummyDiv = jQuery('<div/>')
			str = dummyDiv.text(str).html();

			dummyDiv.remove();

			return str;
		}

		/*
		 * En caso de no disponer de jQuery, se escapa carácter a carácter
		 * mediante un reemplazar.
		 */ 
		return str.replace("Á","&Aacute;")
		.replace("É","&Eacute;")
		.replace("Í","&Iacute;")
		.replace("Ó","&Oacute;")
		.replace("Ú","&Uacute;")
		.replace("á","&aacute;")
		.replace("é","&eacute;")
		.replace("í","&iacute;")
		.replace("ó","&oacute;")
		.replace("ú","&uacute;")

		.replace("À","&Agrave;")
		.replace("È","&Egrave;")
		.replace("Ì","&Igrave;")
		.replace("Ò","&Ograve;")
		.replace("Ù","&Ugrave;")
		.replace("à","&agrave;")
		.replace("è","&egrave;")
		.replace("ì","&igrave;")
		.replace("ò","&ograve;")
		.replace("ù","&ugrave;")

		.replace("€","&euro;")
		.replace("¥","&yen;")
		.replace("£","&pound;")
		.replace("¢","&cent;")

		.replace("&","&amp;")

		.replace("<","&lt;")
		.replace(">","&gt;")

		.replace("©","&copy;")
		.replace("®","&reg;")

		.replace("µ","&micro;");
	}
	else{
		return str;
	}
}