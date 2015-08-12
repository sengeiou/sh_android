package com.shootr.android.domain.service;

import com.shootr.android.domain.exception.ShootrException;

public class ChangePasswordInvalidException extends ShootrException {

    public ChangePasswordInvalidException(String message) {
        super(message);
    }

    public ChangePasswordInvalidException(Throwable cause) {
        super(cause);
    }

}
