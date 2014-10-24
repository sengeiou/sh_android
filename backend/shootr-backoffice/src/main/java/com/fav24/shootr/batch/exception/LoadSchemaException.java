package com.fav24.shootr.batch.exception;


public class LoadSchemaException extends XMLException {
	private static final long serialVersionUID = -3640654909658333091L;

	public LoadSchemaException(String message) {
        super(message);
    }

    public LoadSchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadSchemaException(Throwable cause) {
        super(cause);
    }
}
