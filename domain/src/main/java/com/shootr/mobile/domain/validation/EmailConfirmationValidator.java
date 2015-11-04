package com.shootr.mobile.domain.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmailConfirmationValidator {

    public static final int FIELD_EMAIL = 1;

    private List<FieldValidationError> fieldValidationErrors;

    public EmailConfirmationValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(String email) {
        validateEmail(email);
        return fieldValidationErrors;
    }

    //region Email
    private void validateEmail(String email) {
        validateEmailFormat(email);
        validateEmailIsNotNull(email);
    }

    private void validateEmailIsNotNull(String email) {
        if (email == null) {
            addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL, CreateUserValidator.FIELD_EMAIL);
        }
    }

    private void validateEmailFormat(String email) {
        if (email != null) {
            Pattern emailPattern = Pattern.compile(CreateUserValidator.EMAIL_PATTERN);
            boolean hasEmailFormat = emailPattern.matcher(email).matches();
            if (!hasEmailFormat) {
                addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT, CreateUserValidator.FIELD_EMAIL);
            }
        }
    }
    //endregion

    private void addError(String code, int field) {
        fieldValidationErrors.add(new FieldValidationError(code, field));
    }

}
