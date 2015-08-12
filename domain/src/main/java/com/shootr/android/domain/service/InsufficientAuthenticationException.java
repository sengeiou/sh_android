package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class InsufficientAuthenticationException extends ShootrException {

    public InsufficientAuthenticationException(String message) {
        super(message);
    }

    public InsufficientAuthenticationException(Throwable cause) {
        super(cause);
    }

}
