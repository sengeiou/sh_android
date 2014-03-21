package com.fav24.dataservices.xml.security;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.security.EntityData;
import com.fav24.dataservices.domain.security.EntityOrdination;


/**
 * Clase que define la sección Ordination de la definición de las políticas de acceso de una entidad.
 */
public class EntityOrdinationDOM extends EntityOrdination {


	/**
	 * Construye la ordenación para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la ordenación.
	 * @param data Sección data de la definición de polícitas. 
	 */
	public EntityOrdinationDOM(Node node, EntityData data) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Attribute".equals(nodeName)) {
					addOrderAttribute(new EntityOrderAttributeDOM(node_i, data));
				}
			}
		}
	}
}
