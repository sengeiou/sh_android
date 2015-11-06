package com.shootr.mobile.domain.exception;

import com.shootr.mobile.domain.validation.FieldValidationError;
import java.util.Arrays;
import java.util.List;

public class DomainValidationException extends ShootrException {

    private final List<FieldValidationError> errors;

    public DomainValidationException(FieldValidationError... errors) {
        this.errors = Arrays.asList(errors);
    }

    public DomainValidationException(List<FieldValidationError> errors) {
        this.errors = errors;
    }

    public List<FieldValidationError> getErrors() {
        return errors;
    }
}
