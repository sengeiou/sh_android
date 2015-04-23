package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.ShootrException;

public class AccountCreationException extends ShootrException {

    public AccountCreationException(String message) {
        super(message);
    }

    public AccountCreationException(Throwable cause) {
        super(cause);
    }

}
