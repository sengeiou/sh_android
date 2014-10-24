package com.fav24.shootr.batch.exception;


public class ReadXMLException extends XMLException {
	private static final long serialVersionUID = 8248503501343897759L;

	public ReadXMLException(String message) {
        super(message);
    }

    public ReadXMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReadXMLException(Throwable cause) {
        super(cause);
    }
}
