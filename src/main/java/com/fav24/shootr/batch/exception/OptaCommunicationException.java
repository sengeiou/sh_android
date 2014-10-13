package com.fav24.shootr.batch.exception;


public class OptaCommunicationException extends OptaException {
	private static final long serialVersionUID = 6397560878029936346L;

	public OptaCommunicationException(String message) {
        super(message);

    }

    public OptaCommunicationException(String message, Throwable cause) {
        super(message, cause);
    }


}
