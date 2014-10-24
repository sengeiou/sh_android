package com.fav24.shootr.batch.exception;


public class ConstructDOMException extends XMLException {
	private static final long serialVersionUID = 4926421931197975378L;

	public ConstructDOMException(String message) {
        super(message);
    }

    public ConstructDOMException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConstructDOMException(Throwable cause) {
        super(cause);
    }
}
