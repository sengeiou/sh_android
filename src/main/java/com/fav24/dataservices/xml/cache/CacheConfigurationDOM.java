package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.cache.CacheConfiguration;


public class CacheConfigurationDOM extends CacheConfiguration
{

	/**
	 * Construye la configuración de una caché.
	 * 
	 * @param node Nodo de inicio de una caché.
	 * @param parentConfiguration Configuración padre para los valores por defecto.
	 */
	public CacheConfigurationDOM(Node node, CacheConfiguration parentConfiguration) {

		super(parentConfiguration);

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Expiry".equals(nodeName)) {

					setExpiry(new ExpiryDOM(node_i));
				}
				else if ("MaxBytesLocalHeap".equals(nodeName)) {

					setMaxBytesLocalHeap(Long.parseLong(node_i.getTextContent()));
				}
				else if ("MaxEntriesLocalHeap".equals(nodeName)) {

					setMaxEntriesLocalHeap(Long.parseLong(node_i.getTextContent()));
				}
				else if ("MaxBytesLocalDisk".equals(nodeName)) {

					setMaxBytesLocalDisk(Long.parseLong(node_i.getTextContent()));
				}
				else if ("MaxEntriesLocalDisk".equals(nodeName)) {

					setMaxEntriesLocalDisk(Long.parseLong(node_i.getTextContent()));
				}
				else if ("DiskExpiryThreadIntervalSeconds".equals(nodeName)) {

					setDiskExpiryThreadIntervalSeconds(Long.parseLong(node_i.getTextContent()));
				}
				else if ("MemoryStoreEvictionPolicy".equals(nodeName)) {

					setMemoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.fromString(node_i.getTextContent()));
				}
				else if ("Persistence".equals(nodeName)) {

					setPersistence(new PersistenceDOM(node_i));
				}
			}
		}
	}
}
