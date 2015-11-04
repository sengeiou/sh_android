package com.shootr.mobile.domain.exception;

public class InvalidResetPasswordException extends ShootrException {

    public InvalidResetPasswordException(String message) {
        super(message);
    }

    public InvalidResetPasswordException(Throwable cause) {
        super(cause);
    }

}
