package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class CacheManagerConfigurationDOM
{
	private Long maxBytesLocalHeap;
	private Long maxBytesLocalDisk;
	private DiskStoreDOM diskStore;


	/**
	 * Construye la configuración de un gestor de caché.
	 * 
	 * @param node Nodo de inicio de la configuración de caché.
	 */
	public CacheManagerConfigurationDOM(Node node) {

		Element element = (Element) node;

		maxBytesLocalHeap = Long.parseLong(element.getAttribute("MaxBytesLocalHeap"));
		maxBytesLocalDisk = Long.parseLong(element.getAttribute("MaxBytesLocalDisk"));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DiskStore".equals(nodeName)) {

					diskStore = new DiskStoreDOM(node_i);
				}
			}
		}
	}

	/**
	 * Retorna el número máximo de bytes en memoria.
	 * 
	 * @return el número máximo de bytes en memoria.
	 */
	public Long getMaxBytesLocalHeap() {
		return maxBytesLocalHeap;
	}

	/**
	 * Retorna el número máximo de bytes en disco.
	 * 
	 * @return el número máximo de bytes en disco.
	 */
	public Long getMaxBytesLocalDisk() {
		return maxBytesLocalDisk;
	}

	/**
	 * Retorna la ubicación de los ficheros de persistencia de la caché.
	 * 
	 * @return la ubicación de los ficheros de persistencia de la caché.
	 */
	public DiskStoreDOM getDiskStore() {
		return diskStore;
	}
}
