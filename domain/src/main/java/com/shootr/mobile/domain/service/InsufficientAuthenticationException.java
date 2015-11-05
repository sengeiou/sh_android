package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class InsufficientAuthenticationException extends ShootrException {

    public InsufficientAuthenticationException(String message) {
        super(message);
    }

    public InsufficientAuthenticationException(Throwable cause) {
        super(cause);
    }
}
