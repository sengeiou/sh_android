package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class AccountCreationException extends ShootrException {

    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(Throwable cause) {
        super(cause);
    }
}
