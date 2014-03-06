package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.cache.CacheManagerConfiguration;
import com.fav24.dataservices.domain.cache.EntityCacheManager;


public class EntityCacheManagerDOM extends EntityCacheManager
{
	/**
	 * Gestor de caché para entidades.
	 * 
	 * @param node Nodo de inicio del gestor de caché para entidades.
	 * @param defaultCacheManagerConfiguration Configuración por defecto definida por el contexto.
	 */
	public EntityCacheManagerDOM(Node node, CacheManagerConfiguration defaultCacheManagerConfiguration) {

		setParentConfiguration(defaultCacheManagerConfiguration);

		Element element = (Element) node;

		setName(element.getAttribute("Name"));
		if (element.hasAttribute("MaxBytesLocalHeap")) {
			setMaxBytesLocalHeap(StorageSize.fromStringToBytes(element.getAttribute("MaxBytesLocalHeap")));
		}
		else {
			setMaxBytesLocalHeap(DEFAULT_MAX_BYTES_LOCAL_HEAP);
		}

		if (element.hasAttribute("MaxBytesLocalDisk")) {
			setMaxBytesLocalDisk(StorageSize.fromStringToBytes(element.getAttribute("MaxBytesLocalDisk")));
		}
		else {
			setMaxBytesLocalDisk(DEFAULT_MAX_BYTES_LOCAL_DISK);
		}

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DefaultCache".equals(nodeName)) {
					setDefaultCacheConfiguration(new CacheConfigurationDOM(node_i, defaultCacheManagerConfiguration == null ? null : defaultCacheManagerConfiguration.getDefaultCacheConfiguration()));
				}
				else if ("DiskStore".equals(nodeName)) {
					setDiskStore(new DiskStoreDOM(node_i));
				}
				else if ("EntityCache".equals(nodeName)) {
					addEntityCacheConfiguration(new EntityCacheDOM(node_i, getDefaultCacheConfiguration()));
				}
			}
		}
	}
}
