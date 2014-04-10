package com.fav24.dataservices.xml.security;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.security.EntityAccessPolicy;
import com.fav24.dataservices.exception.ServerException;


public class EntityGroupAccessPolicyDOM
{
	private String groupName;
	private Set<EntityAccessPolicy> entitiesAccessPolicies;


	/**
	 * Construye las políticas de acceso para el grupo de entidades.
	 * 
	 * @param node Nodo de inicio del grupo de entidades.
	 * 
	 * @throws ServerException 
	 */
	public EntityGroupAccessPolicyDOM(Node node) throws ServerException {

		entitiesAccessPolicies = new HashSet<EntityAccessPolicy>();

		groupName = ((Element) node).getAttribute("Name");

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Entity".equals(nodeName)) {
					entitiesAccessPolicies.add(new EntityAccessPolicyDOM(node_i));
				}
			}
		}
	}

	/**
	 * Retorna el nombre de este grupo de políticas de acceso a entidades.
	 * 
	 * @return el nombre de este grupo de políticas de acceso a entidades.
	 */
	public String getGroupName() {

		return groupName;
	}

	/**
	 * Retorna el conjunto de políticas de acceso a entidades, para este grupo.
	 * 
	 * @return el conjunto de políticas de acceso a entidades, para este grupo.
	 */
	public Set<EntityAccessPolicy> getEntitiesAccessPolicies() {
		return entitiesAccessPolicies;
	}
}
