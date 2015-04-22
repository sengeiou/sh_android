package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.ShootrException;

public class InvalidAccountCreationException extends ShootrException {

    public InvalidAccountCreationException(String message) {
        super(message);
    }

    public InvalidAccountCreationException(Throwable cause) {
        super(cause);
    }

}
