package com.shootr.android.domain.validation;

import com.shootr.android.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class EmailConfirmationValidator {

    private static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\._%\\-\\+]{1,256}" +
      "@" +
      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
      "(" +
      "\\." +
      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
      ")+";

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
            addError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL, CreateUserValidator.FIELD_EMAIL);
        }
    }

    private void validateEmailFormat(String email) {
        if (email != null) {
            Pattern emailPattern = Pattern.compile(EMAIL_PATTERN);
            boolean hasEmailFormat = emailPattern.matcher(email).matches();
            if (!hasEmailFormat) {
                addError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT, CreateUserValidator.FIELD_EMAIL);
            }
        }
    }
    //endregion

    private void addError(String code, int field) {
        fieldValidationErrors.add(new FieldValidationError(code, field));
    }

}
