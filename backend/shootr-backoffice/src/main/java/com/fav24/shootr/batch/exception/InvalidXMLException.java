package com.fav24.shootr.batch.exception;


public class InvalidXMLException extends XMLException {
	private static final long serialVersionUID = -4013168742545119168L;

	public InvalidXMLException(String message) {
        super(message);
    }

    public InvalidXMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidXMLException(Throwable cause) {
        super(cause);
    }
}
