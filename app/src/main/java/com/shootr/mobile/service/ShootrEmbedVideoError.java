package com.shootr.mobile.service;

import com.shootr.mobile.domain.exception.ShootrError;

public class ShootrEmbedVideoError implements ShootrError {

    private String errorCode;
    private String message;

    public ShootrEmbedVideoError() {
        /* empty constructor */
    }

    public ShootrEmbedVideoError(String errorCode) {
        this.setErrorCode(errorCode);
    }

    public ShootrEmbedVideoError(String errorCode, String message) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
    }

    @Override public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
