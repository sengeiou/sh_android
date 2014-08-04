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