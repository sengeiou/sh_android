package com.fav24.dataservices.xml.datasource;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.fav24.dataservices.domain.datasource.ConnectionAcquire;


public class ConnectionAcquireDOM extends ConnectionAcquire
{
	/**
	 * Constructor con parámetro.
	 * 
	 * @param node Nodo con contenido de configuración del modo de adquisición de las conexiones por el pool. 
	 */
	public ConnectionAcquireDOM(Node node) {

		Element element = (Element) node;

		setIncrement(Integer.parseInt(element.getAttribute("Increment")));
		setRetries(Integer.parseInt(element.getAttribute("Retries")));
		setRetryDelay(Long.parseLong(element.getAttribute("RetryDelay")));
	}
}
