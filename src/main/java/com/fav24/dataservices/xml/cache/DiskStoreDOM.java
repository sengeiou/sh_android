package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DiskStoreDOM
{
	public String DEFAULT_SYSTEM_TEMP_DIR = "java.io.tmpdir";
	
	private String path;

	
	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración de almacenamiento en disco para la caché. 
	 */
	public DiskStoreDOM(Node node) {
		
		path = ((Element) node).getAttribute("Path");
	}
	
	/**
	 * Retorna la ubicación para los ficheros de caché.
	 * 
	 * @return la ubicación para los ficheros de caché.
	 */
	public String getPath() {
		return path;
	}
}
