package com.fav24.shootr.batch.exception;


public class ShooterException extends Exception {
	private static final long serialVersionUID = -5614642518065692423L;

	public ShooterException(String message) {
        super(message);
    }

    public ShooterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ShooterException(Throwable cause) {
        super(cause);
    }


}
