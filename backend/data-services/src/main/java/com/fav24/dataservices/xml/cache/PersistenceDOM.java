package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.cache.Persistence;


public class PersistenceDOM extends Persistence
{
	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración de persistencia. 
	 */
	public PersistenceDOM(Node node) {

		super(Boolean.parseBoolean(((Element) node).getAttribute("Active")));
	}
}
