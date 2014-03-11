package com.fav24.dataservices.domain.cache;

/**
 * La implementación de esta interfaz, permite que el contenido de la instancia sea reoganizado
 * para garantizar que dos operaciones equivalentes, tiene la misma forma y representación.
 */
public interface Organizable {

	public static final char ELEMENT_SEPARATOR = ':'; 
	
	/**
	 * Organiza el contenido de esta instancia, con el fin de que su versión serializada, sea comparable.
	 * 
	 * @param contentKey Estructura en la se irá construyendo la cadema de texto en forma de clave de contenido.
	 * 
	 * Nota: en caso de que contentKey sea <code>null</code>, se creará internamente.
	 * 
	 * @return una cadena de texto que define de forma única su contenido.
	 */
	public StringBuilder organizeContent(StringBuilder contentKey);
}
