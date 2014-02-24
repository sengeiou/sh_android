package com.fav24.dataservices.xml.security;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.security.EntityKey;


/**
 * Clase que define una clave de la sección Keys de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityKeyDOM extends EntityKey {

	
	/**
	 * Construye una clave para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la clave.
	 */
	public EntityKeyDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Attribute".equals(nodeName)) {
					addKeyAttribute(new EntityAttributeDOM(node_i));
				}
			}
		}
	}
}
