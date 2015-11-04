package com.shootr.mobile.domain.service;

public class EmailInUseException extends com.shootr.mobile.domain.exception.ShootrException {

    public EmailInUseException(String message) {
        super(message);
    }

    public EmailInUseException(Throwable cause) {
        super(cause);
    }

}
