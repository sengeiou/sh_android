package com.fav24.dataservices.xml.security;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.security.EntityAccessPolicy;


public class EntityAccessPolicyDOM extends EntityAccessPolicy
{
	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las políticas de acceso.
	 */
	public EntityAccessPolicyDOM(Node node) {
		
		Element element = (Element) node;
		
		setAllowedOperations(element.getAttribute("AllowedOperations"));
		setOnlyByKey(Boolean.parseBoolean(element.getAttribute("OnlyByKey")));
		setOnlySpecifiedFilters(Boolean.parseBoolean(element.getAttribute("OnlySpecifiedFilters")));
		setMaxPageSize(Long.parseLong(element.getAttribute("MaxPageSize")));
		
		NodeList nodes_i = node.getChildNodes();
		
		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);
			
			if (node_i.getNodeType() == Node.ELEMENT_NODE) {
				
				String nodeName = node_i.getNodeName();
				
				if ("Name".equals(nodeName)) {
					setName(new EntityAttributeDOM(node_i));
				}
				else if ("Data".equals(nodeName)) {
					setData(new EntityDataDOM(node_i));
				}
				else if ("Keys".equals(nodeName)) {
					setKeys(new EntityKeysDOM(node_i));
				}
				else if ("Filters".equals(nodeName)) {
					setFilters(new EntityFiltersDOM(node_i));
				}
			}
		}
	}

	/**
	 * Asigna el conjunto de operaciones permitidas sobre esta entidad.
	 * 
	 * @param allowedOperations El conjunto de operaciones permitidas a asignar en 
	 * 							forma de literales de operaciones separados por espacios en blanco.
	 */
	private void setAllowedOperations(String allowedOperations) {
		
		String[] splittedAllowedOperations = allowedOperations.toLowerCase().trim().split(" ");
		
		for (String operation : splittedAllowedOperations) {
			getAllowedOperations().add(OperationType.fromString(operation.trim()));
		}
	}
}
