package com.fav24.dataservices.xml.policy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.policy.EntityAttribute;


/**
 * Esta clase contiene la definición de un atributo básico. 
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
