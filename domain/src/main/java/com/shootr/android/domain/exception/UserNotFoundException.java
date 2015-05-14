package com.shootr.android.domain.exception;

public class UserNotFoundException extends ShootrException{

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }

}
