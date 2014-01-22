package com.fav24.dataservices.xml;

import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.fav24.dataservices.exception.ParserException;


/**
 * Clase de ayuda a la carga de esquemas XSD
 * usando el CamaleonResourceResolver para la resolución de includes.
 */
public class SchemaLoader 
{
	public static final String ERROR_INVALID_SCHEMA = "XSD000";
	
	/**
	 * Carga el esquema indicado por parámetro.
	 *  
	 * @param xsd Stream correspondiente al esquema a cargar.
	 * 
	 * @return el esquema indicado por parámetro construido.
	 * 
	 * @throws ParserException En caso de no ser un esquema válido.
	 */
	public static Schema loadSchema(InputStream xsd) throws ParserException 
	{
		Schema schema = null;

		SchemaFactory xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		xsdFactory.setResourceResolver(new ResourceResolver());
		try {
			schema = xsdFactory.newSchema(new StreamSource(xsd));
		} 
		catch (SAXException e) {
			throw new ParserException(ERROR_INVALID_SCHEMA, "El esquema indicado no es válido. (" + e.getMessage() + ").");
		}

		return schema;
	}
}
