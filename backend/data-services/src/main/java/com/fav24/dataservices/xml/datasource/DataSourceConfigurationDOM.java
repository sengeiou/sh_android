package com.fav24.dataservices.xml.datasource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.datasource.DataSourceConfiguration;


public class DataSourceConfigurationDOM extends DataSourceConfiguration
{
	/**
	 * Lee, interpreta y construye la configuración de una fuente de datos.
	 * 
	 * @param node Nodo del que se obtiene la configuración de una fuente de datos.
	 */
	public DataSourceConfigurationDOM(Node node) {

		setName(((Element)node).getAttribute("Name"));
		
		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("ConnectionPool".equals(nodeName)) {

					setConnectionPoolConfiguration(new ConnectionPoolConfigurationDOM(node_i));
				}
				else if ("Host".equals(nodeName)) {
					
					setHost(node_i.getTextContent());
				}
				else if ("Port".equals(nodeName)) {
					
					setPort(Integer.parseInt(node_i.getTextContent()));
				}
				else if ("DatabaseName".equals(nodeName)) {
					
					setDatabaseName(node_i.getTextContent());
				}
				else if ("UserName".equals(nodeName)) {

					setUserName(node_i.getTextContent());
				}
				else if ("Password".equals(nodeName)) {
					
					setPassword(node_i.getTextContent());
				}
			}
		}
	}
}
