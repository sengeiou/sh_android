package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class ResetPasswordException extends ShootrException {

    public ResetPasswordException(String message) {
        super(message);
    }

    public ResetPasswordException(Throwable cause) {
        super(cause);
    }
}
