package com.shootr.mobile.domain.exception;

public class ShootrValidationException extends ShootrException {

    private final String errorCode;

    public ShootrValidationException(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
