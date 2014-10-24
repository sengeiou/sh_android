package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.cache.DiskStore;


public class DiskStoreDOM extends DiskStore
{
	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración de almacenamiento en disco para la caché. 
	 */
	public DiskStoreDOM(Node node) {
		
		super(((Element) node).getAttribute("Path"));
	}
	}
