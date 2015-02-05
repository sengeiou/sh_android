package com.shootr.android.domain.validation;

public class ValidationError {

    private String errorCode;

    public ValidationError(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
