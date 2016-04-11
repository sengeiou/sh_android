package com.shootr.mobile.domain.validation;

import com.shootr.mobile.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChangePasswordValidator {

    public static final int FIELD_CURRENT_PASSWORD = 1;
    public static final int FIELD_NEW_PASSWORD = 2;
    public static final int FIELD_NEW_PASSWORD_AGAIN = 3;
    private static final int PASSWORD_MINIMUM_LENGTH = 6;
    private static final int PASSWORD_MAXIMUM_LENGTH = 20;
    private static final String PASSWORD_PATTERN = "^([A-Za-z0-9_.,&%€@#~])*$";
    private List<FieldValidationError> fieldValidationErrors;

    public ChangePasswordValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(String currentPassword, String newPassword, String newPasswordAgain,
      String username) {
        validatePassword(currentPassword, newPassword, newPasswordAgain, username);
        return fieldValidationErrors;
    }

    //region Password
    private void validatePassword(String currentPassword, String newPassword, String newPasswordAgain,
      String username) {

        List<String> passwords = Arrays.asList(currentPassword, newPassword, newPasswordAgain);

        for (int field = 0; field < passwords.size(); field++) {
            validatePasswordNotNull(passwords.get(field), field + 1);
            validatePassWordHasNotInvalidCharacters(passwords.get(field), field + 1);
            validatePasswordHasLessThanTwentyCharacters(passwords.get(field), field + 1);
            validatePasswordHasMoreThanSixCharacters(passwords.get(field), field + 1);
        }

        validateNewPasswordIsDifferentFromCurrentPassword(currentPassword, newPassword);
        validateNewPasswordEqualsNewPasswordAgain(newPassword, newPasswordAgain);
        validateNewPasswordIsDifferentUsername(newPassword, username);
    }

    private void validateNewPasswordIsDifferentUsername(String newPassword, String username) {
        if (newPassword != null && newPassword.equals(username)) {
            addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_NEW_PASSWORD_EQUALS_USERNAME,
              FIELD_NEW_PASSWORD_AGAIN);
        }
    }

    private void validatePassWordHasNotInvalidCharacters(String password, int field) {
        if (password != null) {
            Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
            boolean hasPasswordPattern = passwordPattern.matcher(password).matches();
            if (!hasPasswordPattern) {
                addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS,
                  field);
            }
        }
    }

    private void validateNewPasswordIsDifferentFromCurrentPassword(String username, String password) {
        if (username != null && username.equals(password)) {
            addError(ShootrError.ERROR_NEW_PASSWORD_EQUALS_CURRENT_PASSWORD,
              FIELD_NEW_PASSWORD_AGAIN);
        }
    }

    private void validateNewPasswordEqualsNewPasswordAgain(String newPassword, String newPasswordAgain) {
        if (newPassword != null && newPasswordAgain != null && !newPassword.equals(newPasswordAgain)) {
            addError(ShootrError.ERROR_NEW_PASSWORD_NOT_EQUALS_NEW_PASSWORD_AGAIN,
              FIELD_NEW_PASSWORD_AGAIN);
        }
    }

    private void validatePasswordHasLessThanTwentyCharacters(String password, int field) {
        if (password != null && password.length() > PASSWORD_MAXIMUM_LENGTH) {
            addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG,
              field);
        }
    }

    private void validatePasswordHasMoreThanSixCharacters(String password, int field) {
        if (password != null && password.length() < PASSWORD_MINIMUM_LENGTH) {
            addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT,
              field);
        }
    }

    private void validatePasswordNotNull(String password, int field) {
        if (password == null) {
            addError(com.shootr.mobile.domain.exception.ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL, field);
        }
    }
    //endregion

    private void addError(String code, int field) {
        fieldValidationErrors.add(new FieldValidationError(code, field));
    }
}
