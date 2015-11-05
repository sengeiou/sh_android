package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class EmailIsAuthenticatedException extends ShootrException {

    public EmailIsAuthenticatedException(String message) {
        super(message);
    }

    public EmailIsAuthenticatedException(Throwable cause) {
        super(cause);
    }
}
