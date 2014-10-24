package com.fav24.shootr.batch.exception;


public class ConfigureParserException extends XMLException {
	private static final long serialVersionUID = 8750256247123160851L;

	public ConfigureParserException(String message) {
        super(message);
    }

    public ConfigureParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigureParserException(Throwable cause) {
        super(cause);
    }
}
