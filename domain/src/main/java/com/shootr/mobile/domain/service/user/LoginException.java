package com.shootr.mobile.domain.service.user;

public class LoginException extends com.shootr.mobile.domain.exception.ShootrException {

    public LoginException(String message) {
        super(message);
    }

    public LoginException(Throwable cause) {
        super(cause);
    }
}
