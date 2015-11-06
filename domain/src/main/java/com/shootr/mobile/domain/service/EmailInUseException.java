package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class EmailInUseException extends ShootrException {

    public EmailInUseException(String message) {
        super(message);
    }

    public EmailInUseException(Throwable cause) {
        super(cause);
    }
}
