package com.fav24.dataservices.xml.cache;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.cache.CacheConfiguration;
import com.fav24.dataservices.domain.cache.EntityCache;


public class EntityCacheDOM extends EntityCache
{

	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las políticas de acceso.
	 * @param defaultCacheConfiguration Configuración por defecto de los aspectos de la caché.
	 */
	public EntityCacheDOM(Node node, CacheConfiguration defaultCacheConfiguration) {

		super(((Element)node).getAttribute("Alias"), defaultCacheConfiguration);

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
				else if ("Eternal".equals(nodeName)) {

					setEternal(Boolean.parseBoolean(node_i.getTextContent()));
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
