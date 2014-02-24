package com.fav24.dataservices.xml.cache;

import java.io.InputStream;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.xml.BasicDOM;


public class CacheDOM
{
	private static final BasicDOM cacheDOM = new BasicDOM();
	static {
		InputStream isAccessPolicyXSD = CacheDOM.class.getResourceAsStream("AccessPolicy.xsd");

		try {
			
			if (isAccessPolicyXSD == null)
				throw new ServerException("No se ha podido localizar el esquema para la interpretación de configuraciones de caché.");

			cacheDOM.setInputSchemaStream(isAccessPolicyXSD);
			cacheDOM.configureDOM();
			
		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	private String version;
	private String description;
	private CacheManagerConfigurationDOM defaultCacheManagerConfiguration;
	private CacheConfigurationDOM defaultCacheConfiguration;
	private AbstractList<EntityCacheManagerDOM> entityCacheManagers;

	/**
	 * Constructor con parámetro.
	 * 
	 * @param cacheConfiguration URL a un recurso con contenido de configuración de caché. 
	 * 
	 * @throws ServerException
	 */
	public CacheDOM(URL cacheConfiguration) throws ServerException { 

		if (cacheConfiguration == null)
			throw new ServerException("Se ha indicado una URL vacía como origen de configuración de caché.");

		InputStream cacheConfigurationStream;

		try	{
			cacheConfigurationStream = cacheConfiguration.openStream();
		}
		catch (Exception e)	{
			throw new ServerException("No se ha encontrado el fichero: " + cacheConfiguration.getFile());
		}

		//Información relativa al contenido del fichero de configuración de caché.
		Document document = cacheDOM.generateDocument(cacheConfigurationStream);

		readConfiguration(document);
	}
	
	/**
	 * Creación de la estructura de configuraciones de caché, a partir de un stream de estrada.
	 * 
	 * @param cacheConfigurationStream Stream de entrada con contenido de configuración de caché.
	 * 
	 * @throws ServerException
	 */
	public CacheDOM(InputStream cacheConfigurationStream) throws ServerException { 
		
		//Información relativa al contenido del fichero de configuración de caché.
		Document document = cacheDOM.generateDocument(cacheConfigurationStream);
		
		readConfiguration(document);
	}

	/**
	 * Lee, interpreta y construye las estructuras de configuraciones de caché contenidas en el documento indicado.
	 * 
	 * @param document Documento del que se obtienen las configuraciones de caché.
	 */
	private void readConfiguration(Document document) {

		NodeList nodes_i = document.getChildNodes();
		
		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("Cache".equals(nodeName)) {
					readCache(node_i);					
				}
			}
		}
	}

	/**
	 * Lee, interpreta y construye las estructuras de configuraciones de caché contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las configuraciones de caché.
	 */
	private void readCache(Node node) {
		
		Element element = (Element)node;
		
		this.version = element.getAttribute("Version");
		this.description = element.getAttribute("Description");
		
		NodeList nodes_i = node.getChildNodes();
		
		entityCacheManagers = new ArrayList<EntityCacheManagerDOM>();
		
		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DefaultCacheManager".equals(nodeName)) {
					
					defaultCacheManagerConfiguration = new CacheManagerConfigurationDOM(node_i);
				}
				else if ("DefaultCache".equals(nodeName)) {
					
					defaultCacheConfiguration = new CacheConfigurationDOM(node_i);
				}
				else if ("EntityCacheManager".equals(nodeName)) {
					
					entityCacheManagers.add(new EntityCacheManagerDOM(node, defaultCacheManagerConfiguration,
					defaultCacheConfiguration));
				}
			}
		}
	}
	
	/**
	 * Retorna la versión de data-services para la que fué creado esta configuración de caché.
	 *  
	 * @return la versión de data-services para la que fué creado esta configuración de caché.
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Retorna una descripción de esta configuración de caché.
	 * 
	 * @return una descripción de esta configuración de caché.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Retorna la lista de gestores de caché de entidades.
	 * 
	 * @return la lista de gestores de caché de entidades.
	 */
	public AbstractList<EntityCacheManagerDOM> getEntityCacheManagers() {
		return entityCacheManagers;
	}
}
