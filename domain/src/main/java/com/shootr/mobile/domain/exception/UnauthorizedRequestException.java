package com.shootr.mobile.domain.exception;

public class UnauthorizedRequestException extends ShootrExplicitException {

    public UnauthorizedRequestException(Throwable cause) {
        super(cause);
    }
}
