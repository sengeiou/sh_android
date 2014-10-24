package com.fav24.shootr.batch.rest.parser;

import org.w3c.dom.Document;

public interface RequestorStrategy {

	public String generateRequestURL(Requestor.LanguageRequest languageRequest, long... id);

	public <T> T mapResponse(Document document) throws Exception;

	public String getValidationSchema();
}
