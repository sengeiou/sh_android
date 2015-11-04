package com.shootr.mobile.domain.service;

import com.shootr.mobile.domain.exception.ShootrException;

public class ChangePasswordInvalidException extends ShootrException {

    public ChangePasswordInvalidException(String message) {
        super(message);
    }

    public ChangePasswordInvalidException(Throwable cause) {
        super(cause);
    }

}
