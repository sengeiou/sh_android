package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.cache.CacheManagerConfiguration;


public class CacheManagerConfigurationDOM extends CacheManagerConfiguration
{
	/**
	 * Construye la configuración de un gestor de caché.
	 * 
	 * @param node Nodo de inicio de la configuración de caché.
	 * @param parentConfiguration Configuración por defecto definida por su antecesor.
	 */
	public CacheManagerConfigurationDOM(Node node, CacheManagerConfiguration parentConfiguration) {

		super(parentConfiguration);
		
		Element element = (Element) node;

		setMaxBytesLocalHeap(Long.parseLong(element.getAttribute("MaxBytesLocalHeap")));
		setMaxBytesLocalDisk(Long.parseLong(element.getAttribute("MaxBytesLocalDisk")));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DefaultCache".equals(nodeName)) {
					
					setDefaultCacheConfiguration(new CacheConfigurationDOM(node_i, parentConfiguration == null ? null : parentConfiguration.getDefaultCacheConfiguration()));
				}
				else if ("DiskStore".equals(nodeName)) {

					setDiskStore(new DiskStoreDOM(node_i));
				}
			}
		}
	}
}
