package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class SendPasswordResetEmailException extends ShootrException {

    public SendPasswordResetEmailException(String message) {
        super(message);
    }

    public SendPasswordResetEmailException(Throwable cause) {
        super(cause);
    }
}
