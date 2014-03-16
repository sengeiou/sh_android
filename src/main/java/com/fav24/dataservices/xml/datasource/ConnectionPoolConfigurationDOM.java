package com.fav24.dataservices.xml.datasource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.datasource.ConnectionPoolConfiguration;


public class ConnectionPoolConfigurationDOM extends ConnectionPoolConfiguration
{
	/**
	 * Lee, interpreta y construye la configuración de un pool de conexiones.
	 * 
	 * @param node Nodo del que se obtiene la configuración del pool de conexiones.
	 */
	public ConnectionPoolConfigurationDOM(Node node) {

		Element element = (Element) node;

		setIdleTimeout(Long.parseLong(element.getAttribute("IdleTimeout")));
		setMaxLifetime(Long.parseLong(element.getAttribute("MaxLifetime")));
		setMinimumPoolSize(Integer.parseInt(element.getAttribute("MinimumPoolSize")));
		setMaximumPoolSize(Integer.parseInt(element.getAttribute("MaximumPoolSize")));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("ConnectionAcquire".equals(nodeName)) {

					setConnectionAcquire(new ConnectionAcquireDOM(node_i));
				}
				else if ("Connection".equals(nodeName)) {

					setConnectionConfiguration(new ConnectionConfigurationDOM(node_i));
				}
				else if ("DataSourceClassName".equals(nodeName)) {

					setDataSourceClassName(node_i.getTextContent());
				}
			}
		}
	}
}
