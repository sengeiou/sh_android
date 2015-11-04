package com.shootr.mobile.task.validation;

import java.util.ArrayList;
import java.util.List;

public abstract class FieldValidator {

    private List<FieldValidationError> fieldValidationErrors;

    protected FieldValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public final List<FieldValidationError> validate() {
        performValidations();
        return fieldValidationErrors;
    }

    protected abstract int getField();

    protected abstract void performValidations();

    protected final void addError(String errorCode) {
        fieldValidationErrors.add(new FieldValidationError(errorCode, getField()));
    }
}
