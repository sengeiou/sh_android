package com.shootr.mobile.domain.service;

public class SendPasswordResetEmailException extends com.shootr.mobile.domain.exception.ShootrException {

    public SendPasswordResetEmailException(String message) {
        super(message);
    }

    public SendPasswordResetEmailException(Throwable cause) {
        super(cause);
    }

}
