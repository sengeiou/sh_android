package com.shootr.android.service;

public class ShootrError {

    public static final String ERROR_CODE_UNKNOWN_ERROR = "SHTR_00000";
    public static final String ERROR_CODE_INVALID_IMAGE = "SHTR_10001";
    public static final String ERROR_CODE_INVALID_SESSION_TOKEN = "SHTR_10002";

    public static final String ERROR_CODE_USERNAME_DUPLICATE = "U001";
    public static final String ERROR_CODE_USERNAME_NULL = "U002";
    public static final String ERROR_CODE_USERNAME_TOO_SHORT = "U003";
    public static final String ERROR_CODE_USERNAME_TOO_LONG = "U004";
    public static final String ERROR_CODE_USERNAME_INVALID_CHARACTERS = "U005";

    public static final String ERROR_CODE_NAME_TOO_SHORT = "U006";
    public static final String ERROR_CODE_NAME_TOO_LONG = "U007";
    public static final String ERROR_CODE_NAME_INVALID_CHARACTERS = "U008";

    public String errorCode;
    public String message;
    public String explanation;

    private Exception cause;

    public ShootrError() {
        /* empty constructor */
    }

    public ShootrError(String errorCode) {
        this.errorCode = errorCode;
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
