package com.fav24.shootr.batch.exception;


public class XMLException extends ShooterException {
	private static final long serialVersionUID = -9033919246588197008L;

	public XMLException(String message) {
        super(message);
    }

    public XMLException(String message, Throwable cause) {
        super(message, cause);
    }

    public XMLException(Throwable cause) {
        super(cause);
    }
}
