package com.shootr.android.task.validation;

public class FieldValidationError {

    public static final int FIELD_USERNAME = 1;
    public static final int FIELD_NAME = 2;
    public static final int FIELD_WEBSITE = 3;
    public static final int FIELD_BIO = 4;

    private String errorCode;
    private int field;

    public FieldValidationError(String errorCode, int field) {
        this.errorCode = errorCode;
        this.field = field;
    }

    public int getField() {
        return field;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
