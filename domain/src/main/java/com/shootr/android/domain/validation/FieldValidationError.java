package com.shootr.android.domain.validation;

public class FieldValidationError extends ValidationError {

    private final int field;

    public FieldValidationError(String errorCode, int field) {
        super(errorCode);
        this.field = field;
    }

    public int getField() {
        return field;
    }
}
