package com.fav24.dataservices.xml.datasource;

import java.io.InputStream;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fav24.dataservices.domain.datasource.DataSources;
import com.fav24.dataservices.exception.ServerException;
import com.fav24.dataservices.xml.BasicDOM;


public class DataSourcesDOM extends DataSources
{
	private static final BasicDOM dataSourceDOM = new BasicDOM();
	static {
		InputStream isDataSourceXSD = DataSourcesDOM.class.getResourceAsStream("DataSource.xsd");

		try {

			if (isDataSourceXSD == null)
				throw new ServerException("No se ha podido localizar el esquema para la interpretación de configuraciones de las fuentes de datos.");

			dataSourceDOM.setInputSchemaStream(isDataSourceXSD);
			dataSourceDOM.configureDOM();

		} catch (ServerException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Constructor con parámetro.
	 * 
	 * @param dataSourceConfiguration URL a un recurso con contenido de configuración de las fuentes de datos. 
	 * 
	 * @throws ServerException
	 */
	public DataSourcesDOM(URL dataSourceConfiguration) throws ServerException { 

		if (dataSourceConfiguration == null)
			throw new ServerException("Se ha indicado una URL vacía como origen de configuración de las fuentes de datos.");

		InputStream cacheConfigurationStream;

		try	{
			cacheConfigurationStream = dataSourceConfiguration.openStream();
		}
		catch (Exception e)	{
			throw new ServerException("No se ha encontrado el fichero: " + dataSourceConfiguration.getFile());
		}

		//Información relativa al contenido del fichero de configuración de las fuentes de datos.
		Document document = dataSourceDOM.generateDocument(cacheConfigurationStream);

		readConfiguration(document);
	}

	/**
	 * Creación de la estructura de configuraciones de las fuentes de datos, a partir de un stream de entrada.
	 * 
	 * @param dataSourceConfigurationStream Stream de entrada con contenido de configuración de las fuentes de datos.
	 * 
	 * @throws ServerException
	 */
	public DataSourcesDOM(InputStream dataSourceConfigurationStream) throws ServerException { 

		//Información relativa al contenido del fichero de configuración de las fuentes de datos.
		Document document = dataSourceDOM.generateDocument(dataSourceConfigurationStream);

		readConfiguration(document);
	}

	/**
	 * Lee, interpreta y construye las estructuras de configuraciones de las fuentes de datos contenidas en el documento indicado.
	 * 
	 * @param document Documento del que se obtienen las configuraciones de las fuentes de datos.
	 */
	private void readConfiguration(Document document) {

		NodeList nodes_i = document.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DataSources".equals(nodeName)) {
					readDataSources(node_i);					
				}
			}
		}
	}

	/**
	 * Lee, interpreta y construye las estructuras de configuraciones de las fuentes de datos contenidas en el nodo indicado.
	 * 
	 * @param node Nodo del que se obtienen las configuraciones de las fuentes de datos.
	 */
	private void readDataSources(Node node) {

		Element element = (Element)node;

		setVersion(element.getAttribute("Version"));
		setDescription(element.getAttribute("Description"));

		NodeList nodes_i = node.getChildNodes();

		for(int i=0; i < nodes_i.getLength(); i++) {

			Node node_i = nodes_i.item(i);

			if (node_i.getNodeType() == Node.ELEMENT_NODE) {

				String nodeName = node_i.getNodeName();

				if ("DataServices".equals(nodeName)) {

					setDataServices(new DataSourceConfigurationDOM(node_i));
				}
				else if ("Statistics".equals(nodeName)) {

					setStatistics(new DataSourceConfigurationDOM(node_i));
				}
			}
		}
	}
}
