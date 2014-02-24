package com.fav24.dataservices.xml.security;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.security.EntityFilters;


/**
 * Clase que define la sección Filters de la definición de las políticas de acceso de una entidad.
 * 
 * @author Fav24
 */
public class EntityFiltersDOM extends EntityFilters {

	
	/**
	 * Construye la sección Filters para una cierta entidad.
	 * 
	 * @param node Nodo de inicio de la sección.
	 */
	public EntityFiltersDOM(Node node) {

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Filter".equals(nodeName)) {
					getFilters().add(new EntityFilterDOM(node_i));
				}
			}
		}
	}
}
