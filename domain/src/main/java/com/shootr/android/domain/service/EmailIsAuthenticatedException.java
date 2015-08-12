package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class EmailIsAuthenticatedException extends ShootrException {

    public EmailIsAuthenticatedException(String message) {
        super(message);
    }

    public EmailIsAuthenticatedException(Throwable cause) {
        super(cause);
    }

}
