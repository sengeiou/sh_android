package com.fav24.dataservices.xml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.security.EntityFilter;


/**
 * Clase que define un filtro de la sección Filters de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityFilterDOM extends EntityFilter {


	/**
	 * Construye un filtro para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la clave.
	 */
	public EntityFilterDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Attribute".equals(nodeName)) {
					getFilter().add(new EntityAttributeDOM(node_i));
				}
			}
		}
	}
}
