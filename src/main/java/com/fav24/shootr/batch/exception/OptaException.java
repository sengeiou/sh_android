package com.fav24.shootr.batch.exception;


public class OptaException extends ShooterException {
	private static final long serialVersionUID = 2402574873201166548L;

	public OptaException(String message) {
        super(message);
    }

    public OptaException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptaException(Throwable cause) {
        super(cause);
    }
}
