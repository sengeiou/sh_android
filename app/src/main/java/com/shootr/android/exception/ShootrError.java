package com.shootr.android.exception;

public interface ShootrError {

    public static final String OK = "OK";

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
    public static final String ERROR_CODE_WEBSITE_WRONG_URI = "U009";
    public static final String ERROR_CODE_WEBSITE_TOO_LONG = "U010";
    public static final String ERROR_CODE_BIO_TOO_SHORT = "U011";
    public static final String ERROR_CODE_BIO_TOO_LONG = "U012";
    public static final String ERROR_CODE_TEAM_ID_INVALID = "U015";

    public String getErrorCode();

    String getMessage();
}
