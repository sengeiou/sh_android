package com.fav24.dataservices.xml.cache;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class EntityCacheManagerDOM extends CacheManagerConfigurationDOM
{
	private String name;
	private Set<EntityCacheDOM> entitiesCacheConfigurations;


	/**
	 * Gestor de caché para entidades.
	 * 
	 * @param node Nodo de inicio del gestor de caché para entidades.
	 */
	public EntityCacheManagerDOM(Node node, CacheManagerConfigurationDOM defaultCacheManagerConfiguration,
	CacheConfigurationDOM defaultCacheConfiguration) {

		super(node);
		
		entitiesCacheConfigurations = new HashSet<EntityCacheDOM>();
		
		name = ((Element) node).getAttribute("Name");

		NodeList nodes_i = node.getChildNodes();
		
		for(int i=0; i < nodes_i.getLength(); i++) {
			
			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {
				
				String nodeName = node_i.getNodeName();

				if ("EntityCache".equals(nodeName)) {
					entitiesCacheConfigurations.add(new EntityCacheDOM(node_i, defaultCacheConfiguration));
				}
			}
		}
	}

	/**
	 * Retorna el nombre de este grupo de políticas de acceso a entidades.
	 * 
	 * @return el nombre de este grupo de políticas de acceso a entidades.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retorna el conjunto de configuraciones de caché de entidades, para este gestor.
	 * 
	 * @return el conjunto de configuraciones de caché de entidades, para este gestor.
	 */
	public Set<EntityCacheDOM> getEntitiesCacheConfigurations() {
		return entitiesCacheConfigurations;
	}
}
