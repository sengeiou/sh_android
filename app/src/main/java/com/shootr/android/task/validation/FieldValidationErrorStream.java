package com.shootr.android.task.validation;

import java.util.List;

public class FieldValidationErrorStream {

    private List<FieldValidationError> fieldValidationErrors;

    public FieldValidationErrorStream(List<FieldValidationError> fieldValidationErrors) {
        this.fieldValidationErrors = fieldValidationErrors;
    }

    public List<FieldValidationError> getFieldValidationErrors() {
        return fieldValidationErrors;
    }
}
