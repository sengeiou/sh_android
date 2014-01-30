package com.fav24.dataservices.xml;

import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.security.AccessPolicy;


public class AccessPolicyDOM extends AccessPolicy
{
	private static final BasicDOM accessPolicyDOM = new BasicDOM();
	static {
		InputStream isAccessPolicyXSD = AccessPolicyDOM.class.getResourceAsStream("AccessPolicy.xsd");

		try {
			
			if (isAccessPolicyXSD == null)
				throw new ServerException("No se ha podido localizar el esquema para la interpretación de políticas de acceso.");

			accessPolicyDOM.setInputSchemaStream(isAccessPolicyXSD);
			accessPolicyDOM.configureDOM();
			
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	private String version;
	private String description;
	private AbstractList<EntityGroupAccessPolicyDOM> entityGroupsAccessPolicies;

	/**
	 * Constructor con parámetro.
	 * 
	 * @param accessPolicies URL a un recurso con contenido de políticas de acceso. 
	 * 
	 * @throws ServerException
	 */
	public AccessPolicyDOM(URL accessPolicies) throws ServerException { 

		if (accessPolicies == null)
			throw new ServerException("Se ha indicado una URL vacía como origen de políticas de acceso.");

		InputStream accessPoliciesStream;

		try	{
			accessPoliciesStream = accessPolicies.openStream();
		}
		catch (Exception e)	{
			throw new ServerException("No se ha encontrado el fichero: " + accessPolicies.getFile());
		}

		//Información relativa al contenido del fichero de políticas.
		Document document = accessPolicyDOM.generateDocument(accessPoliciesStream);

		readPolicies(document);
	}

	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el documento indicado.
	 * 
	 * @param document Documento del que se obtienen las políticas de acceso.
	 */
	private void readPolicies(Document document) {

		NodeList nodes_i = document.getChildNodes();
		
		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("AccessPolicy".equals(nodeName)) {
					readAcessPolicy(node_i);					
				}
			}
		}
	}

	/**
	 * Lee, interpreta y construye las estructuras de políticas de acceso contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las políticas de acceso.
	 */
	private void readAcessPolicy(Node node) {
		
		Element element = (Element)node;
		
		this.version = element.getAttribute("Version");
		this.description = element.getAttribute("Description");
		
		NodeList nodes_i = node.getChildNodes();
		
		entityGroupsAccessPolicies = new ArrayList<EntityGroupAccessPolicyDOM>();
		
		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Entities".equals(nodeName)) {
					
					readEntityGroups(node_i);
				}
			}
		}
	}
	
	/**
	 * Lee, interpreta y construye las estructuras de las entidades contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las entidades y grupos de entidades.
	 */
	private void readEntityGroups(Node node) {
		
		EntityGroupAccessPolicyDOM group = new EntityGroupAccessPolicyDOM(node);
		entityGroupsAccessPolicies.add(group);
		
		mergeAccesPolicy(group.getEntitiesAccessPolicies());
	}
	
	/**
	 * Retorna la versión de data-services para la que fué creado esta configuración de políticas de acceso.
	 *  
	 * @return la versión de data-services para la que fué creado esta configuración de políticas de acceso.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Retorna una descripción de esta configuración de políticas de acceso.
	 * 
	 * @return una descripción de esta configuración de políticas de acceso.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retorna la lista de grupos de políticas de acceso a entidades.
	 * 
	 * @return la lista de grupos de políticas de acceso a entidades.
	 */
	public AbstractList<EntityGroupAccessPolicyDOM> getEntityGroupsAccessPolicies() {
		return entityGroupsAccessPolicies;
	}
}
