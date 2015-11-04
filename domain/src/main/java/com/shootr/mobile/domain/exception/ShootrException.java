package com.shootr.mobile.domain.exception;

public abstract class ShootrException extends RuntimeException {

    protected ShootrException() {
    }

    protected ShootrException(String message) {
        super(message);
    }

    protected ShootrException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ShootrException(Throwable cause) {
        super(cause);
    }

    protected ShootrException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
