/*
 * Función que retorna el contenido del objeto indicado, 
 * en forma de cadena de texto.
 */
function printObject(o) {
	var out = '';
	for (var p in o) {
		out += p + ': ' + o[p] + '\n';
	}
	
	return out;
}
