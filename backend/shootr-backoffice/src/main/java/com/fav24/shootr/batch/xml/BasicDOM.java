package com.fav24.shootr.batch.xml;


import com.fav24.shootr.batch.exception.*;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import java.io.IOException;
import java.io.InputStream;


public class BasicDOM {

    public static final String ERROR_CONFIGURING_PARSER = "XML001";
    public static final String ERROR_INVALID_XML = "XML002";
    public static final String ERROR_READING_XML = "XML003";
    public static final String ERROR_CONSTRUCTING_DOM = "XML004";


    private SchemaFactory xsdFactory;
    private DocumentBuilderFactory xmlFactory;
    private DocumentBuilder xmlDocumentBuilder;
    private Validator errorHandler;

    protected Schema schema;


    private class Validator extends DefaultHandler {
        public boolean validationError = false;
        public SAXParseException saxParseException = null;


        public void error(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;

            throw saxParseException;
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            validationError = true;
            saxParseException = exception;

            throw saxParseException;
        }

        public void warning(SAXParseException exception) throws SAXException {
            saxParseException = exception;

            throw saxParseException;
        }

        public boolean hasValidationError() {
            return validationError;
        }
    }

    public BasicDOM() throws ShooterException {
        xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    /**
     * Para asignar el esquema de validación en base a un stream XSD.
     *
     * @param inputXSD Stream XSD a utilizar como esquema de validación.
     * @return El esquema de validación construido.
     * @throws ShooterException
     */
    private Schema setInputSchemaStream(InputStream inputXSD) throws ShooterException {
        try {
            StreamSource ssXSDStream = new StreamSource();

            ssXSDStream.setInputStream(inputXSD);

            if (xsdFactory == null) {
                xsdFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            }

            schema = xsdFactory.newSchema(ssXSDStream);

            try {
                inputXSD.close();
            } catch (IOException e) {
            }

            return schema;
        } catch (SAXException e) {
            throw new LoadSchemaException("No se ha podido cargar el esquema. (" + e.getMessage() + ")", e.getCause());
        }
    }


    public void configureDOM(InputStream inputXSD) throws ShooterException {
        schema = setInputSchemaStream(inputXSD);
        xmlFactory = DocumentBuilderFactory.newInstance();

        try {
            xmlFactory.setValidating(false);
            xmlFactory.setSchema(schema);

            xmlDocumentBuilder = xmlFactory.newDocumentBuilder();
            xmlDocumentBuilder.setErrorHandler(new Validator());
        } catch (ParserConfigurationException e) {
            throw new ConfigureParserException("Problema al configurar el parser. (" + e.getMessage() + ").", e.getCause());
        }
    }


    public Document generateDocument(InputStream isXMLStream) throws ShooterException {


        Document document;

        try {
            document = xmlDocumentBuilder.parse(isXMLStream);

        } catch (SAXParseException e) {

            StringBuilder message = new StringBuilder();

            message.append("Error de parseo en la línea ").append(e.getLineNumber());
            message.append(" columna ").append(e.getColumnNumber());
            message.append("\nConclusión del parser:\n");
            message.append(e.getMessage());

            throw new InvalidXMLException(message.toString(), e.getCause());
        } catch (IOException e) {
            throw new ReadXMLException("Problema al leer el documento. (" + e.getMessage() + ")");
        } catch (Exception e) {
            throw new ConstructDOMException("Problema al parsear el documento. (" + e.getMessage() + ")");
        } finally {
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
