package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.cache.Expiry;


public class ExpiryDOM extends Expiry
{

	/**
	 * Lee, interpreta y construye los tiempos de vida de un elemento.
	 * 
	 * @param node Nodo del que se obtienen los tiempos de vida de un elemento.
	 */
	public ExpiryDOM(Node node) {

		super(Boolean.parseBoolean(((Element)node).getAttribute("Eternal")));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("TimeToIdleSeconds".equals(nodeName)) {

					setTimeToIdleSeconds(Long.parseLong(node_i.getTextContent()));
				}
				else if ("TimeToLiveSeconds".equals(nodeName)) {

					setTimeToLiveSeconds(Long.parseLong(node_i.getTextContent()));
				}
			}
		}
	}
}
