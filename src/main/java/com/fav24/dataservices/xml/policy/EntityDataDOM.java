package com.fav24.dataservices.xml.policy;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.policy.EntityData;


/**
 * Clase que define la sección Data de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityDataDOM extends EntityData {

	
	/**
	 * Construye la sección Data para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la sección.
	 */
	public EntityDataDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Attribute".equals(nodeName)) {
					addDataAttribute(new EntityDataAttributeDOM(node_i));
				}
			}
		}
	}
}
