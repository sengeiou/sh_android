package com.shootr.mobile.domain.service;

public class EmailIsAuthenticatedException extends com.shootr.mobile.domain.exception.ShootrException {

    public EmailIsAuthenticatedException(String message) {
        super(message);
    }

    public EmailIsAuthenticatedException(Throwable cause) {
        super(cause);
    }

}
