package com.fav24.dataservices.xml.security;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.security.EntityAttribute;


/**
 * Esta clase contiene la definición de un atributo básico. 
 * 
 * @author Fav24
 */
public class EntityAttributeDOM extends EntityAttribute {

	/**
	 * Construye el atributo.
	 * 
	 * @param node Nodo de inicio del atributo.
	 */
	public EntityAttributeDOM(Node node) {

		setAlias(((Element) node).getAttribute("Alias"));
		setName(node.getTextContent());
	}
}
