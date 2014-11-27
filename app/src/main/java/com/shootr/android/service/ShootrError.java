package com.shootr.android.service;

public class ShootrError {

    public static final String ERROR_CODE_UNKNOWN_ERROR = "SHTR_00000";
    public static final String ERROR_CODE_INVALID_IMAGE = "SHTR_10001";
    public static final String ERROR_CODE_INVALID_SESSION_TOKEN = "SHTR_10002";

    public String errorCode;
    public String message;
    public String explanation;

    private Exception cause;

    public ShootrError() {
        /* empty constructor */
    }

    public ShootrError(String errorCode, String message, String explanation) {
        this.errorCode = errorCode;
        this.message = message;
        this.explanation = explanation;
    }

    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }
}
