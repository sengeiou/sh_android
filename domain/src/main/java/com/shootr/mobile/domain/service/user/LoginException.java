package com.shootr.mobile.domain.service.user;

import com.shootr.mobile.domain.exception.ShootrException;

public class LoginException extends ShootrException {

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }
}
