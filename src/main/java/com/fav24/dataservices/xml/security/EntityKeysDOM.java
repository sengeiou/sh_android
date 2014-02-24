package com.fav24.dataservices.xml.security;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.security.EntityKeys;


/**
 * Clase que define la sección Keys de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityKeysDOM extends EntityKeys {

	
	/**
	 * Construye la sección Keys para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la sección.
	 */
	public EntityKeysDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Key".equals(nodeName)) {
					getKeys().add(new EntityKeyDOM(node_i));
				}
			}
		}
	}
}
