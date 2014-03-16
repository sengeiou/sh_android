package com.fav24.dataservices.xml.datasource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.datasource.ConnectionConfiguration;


public class ConnectionConfigurationDOM extends ConnectionConfiguration
{

	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración de conexión. 
	 */
	public ConnectionConfigurationDOM(Node node) {

		Element element = (Element) node;

		setAutoCommit(Boolean.parseBoolean(element.getAttribute("AutoCommit")));
		setTimeout(Long.parseLong(element.getAttribute("Timeout")));
		setTransactionIsolation(TransactionIsolationType.fromString(element.getAttribute("TransactionIsolation")));
		setJdbc4ConnectionTest(Boolean.parseBoolean(element.getAttribute("Jdbc4ConnectionTest")));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("InitSql".equals(nodeName)) {

					setInitSql(node_i.getTextContent());
				}
				else if ("TestQuery".equals(nodeName)) {

					setTestQuery(node_i.getTextContent());
				}
			}
		}
	}
}
