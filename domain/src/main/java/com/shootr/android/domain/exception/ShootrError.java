package com.shootr.android.domain.exception;

public interface ShootrError {

    String OK = "OK";

    String ERROR_CODE_UPDATE_REQUIRED = "REQ002";

    String ERROR_CODE_UNKNOWN_ERROR = "SHTR_00000";
    String ERROR_CODE_INVALID_IMAGE = "SHTR_10001";
    String ERROR_CODE_INVALID_SESSION_TOKEN = "SHTR_10002";

    String ERROR_CODE_USERNAME_DUPLICATE = "U001";
    String ERROR_CODE_USERNAME_NULL = "U002";
    String ERROR_CODE_USERNAME_TOO_SHORT = "U003";
    String ERROR_CODE_USERNAME_TOO_LONG = "U004";
    String ERROR_CODE_USERNAME_INVALID_CHARACTERS = "U005";
    String ERROR_CODE_NAME_TOO_SHORT = "U006";
    String ERROR_CODE_NAME_TOO_LONG = "U007";
    String ERROR_CODE_NAME_INVALID_CHARACTERS = "U008";
    String ERROR_CODE_WEBSITE_WRONG_URI = "U009";
    String ERROR_CODE_WEBSITE_TOO_LONG = "U010";
    String ERROR_CODE_BIO_TOO_SHORT = "U011";
    String ERROR_CODE_BIO_TOO_LONG = "U012";

    String ERROR_CODE_SEARCH_TOO_SHORT = "???1";

    String ERROR_CODE_SHOT_TEXT_DUPLICATED = "CS002";

    String ERROR_CODE_STREAM_TITLE_TOO_SHORT = "E002";
    String ERROR_CODE_STREAM_TITLE_TOO_LONG = "E003";
    String ERROR_CODE_EVENT_START_DATE_TOO_LATE = "E011";

    String ERROR_CODE_REGISTRATION_USERNAME_DUPLICATE = "US001";
    String ERROR_CODE_REGISTRATION_USERNAME_NULL = "US002";
    String ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT = "US003";
    String ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG = "US004";
    String ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS = "US005";
    String ERROR_CODE_REGISTRATION_EMAIL_IN_USE = "US006";
    String ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT = "US007";
    String ERROR_CODE_REGISTRATION_EMAIL_NULL = "US008";
    String ERROR_CODE_REGISTRATION_PASSWORD_NULL= "US009";

    String ERROR_SUBCODE_TAG_TOO_SHORT = "E017";
    String ERROR_SUBCODE_TAG_TOO_LONG = "E018";


    // Local validation error codes
    String ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT = "US009.1-local";
    String ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG = "US009.2-local";
    String ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME = "US009.3-local";
    String ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS = "US009.4-local";

    String ERROR_SUBCODE_EMPTY_URL = "VE_001";
    String ERROR_SUBCODE_UNRECOGNIZED_VIDEO_TYPE = "VE_002";
    String ERROR_SUBCODE_NO_LINKS_IN_COMMENT = "VE_003";
    String ERROR_SUBCODE_MORE_THAN_ONE_VALID_LINK = "VE_004";


    String getErrorCode();

    String getMessage();
}
