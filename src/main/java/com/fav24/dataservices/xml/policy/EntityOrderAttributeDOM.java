package com.fav24.dataservices.xml.policy;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.policy.EntityData;
import com.fav24.dataservices.domain.policy.EntityOrderAttribute;


/**
 * Esta clase contiene la definición de un atributo de ordenación. 
 */
public class EntityOrderAttributeDOM extends EntityOrderAttribute {

	/**
	 * Construye el atributo.
	 * 
	 * @param node Nodo de inicio del atributo.
	 * @param data Sección data de la definición de polícitas.
	 */
	public EntityOrderAttributeDOM(Node node, EntityData data) {

		setAlias(((Element) node).getAttribute("Alias"));
		setOrder(((Element) node).getAttribute("Order"));
		if (data.hasAttribute(getAlias())) {
			setName(data.getAttribute(getAlias()).getName());
		}
	}

	/**
	 * Asigna el sentido de ordenación de los datos para este atributo.
	 *  
	 * @param order El sentido de ordenación a asignar.
	 */
	private void setOrder(String order) {

		setOrder(Order.fromString(order));
	}
}
