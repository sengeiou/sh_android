package com.shootr.android.domain.exception;

public class InvalidGetUserException extends ShootrException{

    public InvalidGetUserException(String message) {
        super(message);
    }

    public InvalidGetUserException(Throwable cause) {
        super(cause);
    }

}
