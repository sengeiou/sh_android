package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class SendPasswordResetEmailException extends ShootrException {

    public SendPasswordResetEmailException(String message) {
        super(message);
    }

    public SendPasswordResetEmailException(Throwable cause) {
        super(cause);
    }

}
