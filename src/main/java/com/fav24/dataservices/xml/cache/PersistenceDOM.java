package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class PersistenceDOM
{
	private Boolean active;


	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración de persistencia. 
	 */
	public PersistenceDOM(Node node) {

		active = Boolean.parseBoolean(((Element) node).getAttribute("Active"));
	}

	/**
	 * Retorna true o false en función de si la persistencia a disco, está o no activada.
	 * 
	 * @return true o false en función de si la persistencia a disco, está o no activada.
	 */
	public Boolean isActive() {
		return active;
	}
}
