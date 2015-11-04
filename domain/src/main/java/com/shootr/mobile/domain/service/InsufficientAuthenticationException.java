package com.shootr.mobile.domain.service;

public class InsufficientAuthenticationException extends com.shootr.mobile.domain.exception.ShootrException {

    public InsufficientAuthenticationException(String message) {
        super(message);
    }

    public InsufficientAuthenticationException(Throwable cause) {
        super(cause);
    }

}
