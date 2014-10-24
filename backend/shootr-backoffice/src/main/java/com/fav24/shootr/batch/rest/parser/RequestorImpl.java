package com.fav24.shootr.batch.rest.parser;

import com.fav24.shootr.batch.exception.OptaCommunicationException;
import com.fav24.shootr.batch.exception.OptaParsingException;
import com.fav24.shootr.batch.network.NetworkFactory;
import com.fav24.shootr.batch.xml.BasicDOM;

import org.w3c.dom.Document;

import java.io.InputStream;

public class RequestorImpl implements Requestor {

	private RequestorStrategy requestorStrategy;

	private NetworkFactory networkFactory;

	public RequestorImpl(RequestorStrategy requestorStrategy, NetworkFactory networkFactor) {
		this.requestorStrategy = requestorStrategy;
		this.networkFactory = networkFactor;
	}

	@Override
	public <T> T doRequest(LanguageRequest languageRequest, long... id) throws OptaCommunicationException, OptaParsingException {

		InputStream response = null;

		try {
			String requestUrl = requestorStrategy.generateRequestURL(languageRequest, id);
			response = networkFactory.getInputStreamFromUrl(requestUrl);

		} catch (Throwable t) {
			throw new OptaCommunicationException(CONNECTION_ERROR_MESSAGE, t);
		}

		try {

			Document document = getValidatedDocument(response);
			T mappedResponse = requestorStrategy.mapResponse(document);
			return mappedResponse;
		} catch (Throwable t) {
			throw new OptaParsingException(PARSE_ERROR_MESSAGE, t);
		}

	}

	/**
	 * Dado el inputStream generado a partir de la conexión del WebService, se descarga el contenido lo valida contra el esquema que aplica. En caso de no pasar la validación devuelve una excepción.
	 * @param response
	 * @return
	 * @throws Exception
	 */
	private Document getValidatedDocument(InputStream response) throws Exception {

		BasicDOM basicDOM = new BasicDOM();

		InputStream xsdInputStream = getClass().getResource(requestorStrategy.getValidationSchema()).openStream();
		basicDOM.configureDOM(xsdInputStream);

		return basicDOM.generateDocument(response);
	}

}
