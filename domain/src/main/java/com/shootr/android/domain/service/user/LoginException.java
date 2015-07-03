package com.shootr.android.domain.service.user;

import com.shootr.android.domain.exception.ShootrException;

public class LoginException extends ShootrException {

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }
}
