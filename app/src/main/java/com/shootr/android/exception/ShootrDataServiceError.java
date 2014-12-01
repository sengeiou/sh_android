package com.shootr.android.exception;

public class ShootrDataServiceError implements ShootrError {

    private String errorCode;
    private String message;

    public ShootrDataServiceError(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    @Override public String getErrorCode() {
        return errorCode;
    }

    @Override public String getMessage() {
        return message;
    }
}
