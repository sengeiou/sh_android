package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class ResetPasswordException extends ShootrException {

    public ResetPasswordException(String message) {
        super(message);
    }

    public ResetPasswordException(Throwable cause) {
        super(cause);
    }
}
