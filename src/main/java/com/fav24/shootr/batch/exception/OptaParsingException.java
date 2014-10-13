package com.fav24.shootr.batch.exception;


public class OptaParsingException extends OptaException {
	private static final long serialVersionUID = -3537217459504885628L;

	public OptaParsingException(String message) {
        super(message);

    }

    public OptaParsingException(String message, Throwable cause) {
        super(message, cause);
    }


}
