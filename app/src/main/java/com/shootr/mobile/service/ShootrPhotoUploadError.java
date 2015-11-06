package com.shootr.mobile.service;

import com.shootr.mobile.domain.exception.ShootrError;

public class ShootrPhotoUploadError implements ShootrError {

    private String errorCode;
    private String message;
    private String explanation;

    public ShootrPhotoUploadError() {
        /* empty constructor */
    }

    public ShootrPhotoUploadError(String errorCode) {
        this.setErrorCode(errorCode);
    }

    public ShootrPhotoUploadError(String errorCode, String message, String explanation) {
        this.setErrorCode(errorCode);
        this.setMessage(message);
        this.setExplanation(explanation);
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

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
