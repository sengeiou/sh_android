package com.shootr.mobile.domain.service.user;

public class AccountCreationException extends com.shootr.mobile.domain.exception.ShootrException {

    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(Throwable cause) {
        super(cause);
    }

}
