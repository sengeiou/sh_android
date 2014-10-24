package com.fav24.dataservices.xml;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.fav24.dataservices.exception.ServerException;


public class BasicDOM 
{
	public static final String ERROR_LOADING_SCHEMA = "XML000";
	public static final String ERROR_CONFIGURING_PARSER = "XML001";
	public static final String ERROR_INVALID_XML = "XML002";
	public static final String ERROR_READING_XML = "XML003";
	public static final String ERROR_CONSTRUCTING_DOM = "XML004";


	private SchemaFactory xsdFactory;
	private DocumentBuilderFactory xmlFactory;
	private DocumentBuilder xmlDocumentBuilder;
	private Validator errorHandler;

	protected Schema schema;


	private class Validator extends DefaultHandler
	{	
		public boolean validationError = false;	     
		public SAXParseException saxParseException = null;


		public void error(SAXParseException exception) throws SAXException
		{
			validationError = true;	  
			saxParseException = exception;

			throw saxParseException;
		} 

		public void fatalError(SAXParseException exception) throws SAXException
		{
			validationError = true;
			saxParseException = exception;

			throw saxParseException;
		}

		public void warning(SAXParseException exception) throws SAXException
		{
			saxParseException = exception;

			throw saxParseException;
		}

		public boolean hasValidationError()
		{
			return validationError;
		}
	}

	public BasicDOM() 
	{
		xsdFactory = null;
		schema = null;
	}

	/**
	 * Para asignar el esquema de validación en base a un stream XSD.
	 * 
	 * @param inputXSD Stream XSD a utilizar como esquema de validación.
	 * 
	 * @return El esquema de validación construido.
	 * 
	 * @throws ServerException
	 */
	public Schema setInputSchemaStream(InputStream inputXSD) throws ServerException
	{	
		try
		{
			StreamSource ssXSDStream = new StreamSource();

			ssXSDStream.setInputStream(inputXSD);

			if (xsdFactory == null)
			{
				xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				xsdFactory.setResourceResolver(new ResourceResolver());
			}

			schema = xsdFactory.newSchema(ssXSDStream);

			try
			{
				inputXSD.close();
			}
			catch (IOException e) 
			{
			}

			return schema;
		}
		catch (SAXException e)
		{
			throw new ServerException(ERROR_LOADING_SCHEMA, "No se ha podido cargar el esquema. (" + e.getMessage() + ")");
		}
	}

	/**
	 * Para asignar el esquema de validación.
	 * 
	 * @param schema Esquema de validación.
	 * 
	 * @throws ServerException
	 */
	public void setSchema(Schema schema) throws ServerException
	{
		this.schema = schema;
	}

	/**
	 * Para asignar un conjunto de esquemas de validación en base a un array de streams XSD.
	 * 
	 * @param inputXSD Array de streams XSD a utilizar como esquemas de validación.
	 * 
	 * @return El esquema de validación construido.
	 * 
	 * @throws ServerException
	 */
	public Schema setInputSchemaStreams(InputStream inputXSD[]) throws ServerException
	{	
		try
		{
			StreamSource ssXSDStreams[] = new StreamSource[inputXSD.length];

			for(int i=0; i<inputXSD.length; i++)
			{
				ssXSDStreams[i] = new StreamSource();
				ssXSDStreams[i].setInputStream(inputXSD[i]);
			}

			if (xsdFactory == null)
			{
				xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				xsdFactory.setResourceResolver(new ResourceResolver());
			}

			schema = xsdFactory.newSchema(ssXSDStreams);

			try
			{
				for(int i=0; i<inputXSD.length; i++)
				{
					inputXSD[i].close();
				}
			}
			catch (IOException e) 
			{
			}
		}
		catch (SAXException e)
		{
			throw new ServerException(ERROR_LOADING_SCHEMA, "No se han podido cargar los esquemas. (" + e.getMessage() + ")");
		} 

		return schema;
	}

	public void configureDOM() throws ServerException
	{
		xmlFactory = DocumentBuilderFactory.newInstance();

		try
		{
			xmlFactory.setValidating(false);
			xmlFactory.setSchema(schema);

			xmlDocumentBuilder = xmlFactory.newDocumentBuilder();
			xmlDocumentBuilder.setErrorHandler(new Validator());
		}
		catch (ParserConfigurationException e)
		{
			throw new ServerException(ERROR_CONFIGURING_PARSER, "Problema al configurar el parser. (" + e.getMessage() + ").");
		}	
	}

	/**
	 * Retorna el documento XML generado y cierra el stream de entrada.
	 * 
	 * @param isXMLStream El stream a partir del que se genera el documento.
	 * 
	 * @return el documento XML generado y cierra el stream de entrada.
	 * 
	 * @throws ServerException
	 */
	public Document generateDocument(InputStream isXMLStream) throws ServerException {
		Document document;

		try	{
			document = xmlDocumentBuilder.parse(isXMLStream);
		}
		catch (SAXParseException e)	{

			StringBuilder message = new StringBuilder();

			message.append("Error de parseo en la línea ").append(e.getLineNumber());
			message.append(" columna ").append(e.getColumnNumber());
			message.append("\nConclusión del parser:\n");
			message.append(e.getMessage());

			throw new ServerException(ERROR_INVALID_XML, message.toString());
		}
		catch (IOException e) {
			throw new ServerException(ERROR_READING_XML, "Problema al leer el documento. (" + e.getMessage() + ")");
		}
		catch (Exception e) {
			throw new ServerException(ERROR_CONSTRUCTING_DOM, "Problema al parsear el documento. (" + e.getMessage() + ")");
		}
		finally {
			IOUtils.closeQuietly(isXMLStream);
		}

		return document;
	}

	/**
	 * Retorna true o false en función de si ha habido o no errores de validación.
	 * 
	 * @return true o false en función de si ha habido o no errores de validación.
	 */
	public boolean hasValidationError() {
		return errorHandler == null || errorHandler.hasValidationError();
	}
}
