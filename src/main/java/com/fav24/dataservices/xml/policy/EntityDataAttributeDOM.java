package com.fav24.dataservices.xml.policy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.policy.EntityDataAttribute;


/**
 * Esta clase contiene la definición de un atributo dato. 
 * 
 * @author Fav24
 */
public class EntityDataAttributeDOM extends EntityDataAttribute {

	/**
	 * Construye el atributo.
	 * 
	 * @param node Nodo de inicio del atributo.
	 */
	public EntityDataAttributeDOM(Node node) {

		Element element = (Element) node;

		setAlias(element.getAttribute("Alias"));
		setDirection(element.getAttribute("Direction"));
		setName(node.getTextContent());
	}

	/**
	 * Asigna el sentido en el que pueden viajar los datos para este atributo.
	 *  
	 * @param direction El sentido a asignar.
	 */
	private void setDirection(String direction) {

		setDirection(Direction.fromString(direction));
	}
}
