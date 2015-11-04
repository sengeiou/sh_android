package com.shootr.mobile.domain.exception;

import java.util.Arrays;
import java.util.List;

public class DomainValidationException extends ShootrException {

    private final List<com.shootr.mobile.domain.validation.FieldValidationError> errors;

    public DomainValidationException(com.shootr.mobile.domain.validation.FieldValidationError... errors) {
        this.errors = Arrays.asList(errors);
    }

    public DomainValidationException(List<com.shootr.mobile.domain.validation.FieldValidationError> errors) {
        this.errors = errors;
    }

    public List<com.shootr.mobile.domain.validation.FieldValidationError> getErrors() {
        return errors;
    }
}
