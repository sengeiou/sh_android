package com.shootr.mobile.task.validation;

import java.util.List;

public class FieldValidationErrorEvent {

    private List<FieldValidationError> fieldValidationErrors;

    public FieldValidationErrorEvent(List<FieldValidationError> fieldValidationErrors) {
        this.fieldValidationErrors = fieldValidationErrors;
    }

    public List<FieldValidationError> getFieldValidationErrors() {
        return fieldValidationErrors;
    }
}
