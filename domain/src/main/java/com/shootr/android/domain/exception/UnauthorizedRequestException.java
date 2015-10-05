package com.shootr.android.domain.exception;

public class UnauthorizedRequestException extends ShootrExplicitException {

    public UnauthorizedRequestException(Throwable cause) {
        super(cause);
    }
}
