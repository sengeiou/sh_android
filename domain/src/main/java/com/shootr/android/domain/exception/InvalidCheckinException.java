package com.shootr.android.domain.exception;

public class InvalidCheckinException extends ShootrException {

    public InvalidCheckinException(String message) {
        super(message);
    }

    public InvalidCheckinException(Throwable cause) {
        super(cause);
    }
}
