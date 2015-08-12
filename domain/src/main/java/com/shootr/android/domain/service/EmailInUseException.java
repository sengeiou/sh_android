package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class EmailInUseException extends ShootrException {

    public EmailInUseException(String message) {
        super(message);
    }

    public EmailInUseException(Throwable cause) {
        super(cause);
    }

}
