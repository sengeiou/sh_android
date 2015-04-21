package com.shootr.android.domain.validation;

import com.shootr.android.domain.exception.ShootrError;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CreateUserValidator {

    private static final String USERNAME_PATTERN = "^([_A-Za-z0-9])*$";
    private static final int USERNAME_MINIMUM_LENGTH = 3;
    private static final int USERNAME_MAXIMUM_LENGTH = 20;
    private static final int PASSWORD_MINIMUM_LENGTH = 6;
    private static final int PASSWORD_MAXIMUM_LENGTH = 20;
    private static final String PASSWORD_PATTERN = "^([A-Za-z0-9_.,&%€@#~])*$";
    private static final String EMAIL_PATTERN = "[a-zA-Z0-9\\+\\._%\\-\\+]{1,256}" +
      "@" +
      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
      "(" +
      "\\." +
      "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
      ")+";

    public static final int FIELD_EMAIL = 1;
    public static final int FIELD_USERNAME = 2;
    public static final int FIELD_PASSWORD = 3;

    private List<FieldValidationError> fieldValidationErrors;

    public CreateUserValidator() {
        fieldValidationErrors = new ArrayList<>();
    }

    public List<FieldValidationError> validate(String email, String username, String password) {
        validateEmail(email);
        validateUsername(username);
        validatePassword(username, password);
        return fieldValidationErrors;
    }

    private void validateEmail(String email) {
        validateEmailIsNotNull(email);
        validateEmailFormat(email);
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

    //region Username
    private void validateUsername(String username) {
        validateUsernameIsNotNull(username);
        validateUsernameHasMoreThanThreeCharacters(username);
        validateUsernameHasLessThanTwentyCharacters(username);
        validateUsernameHasTheCorrectFormat(username);
    }

    private void validateUsernameHasTheCorrectFormat(String username) {
        if(username != null) {
            Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);
            boolean hasUsernameFormat = usernamePattern.matcher(username).matches();
            if(!hasUsernameFormat){
                addError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS, FIELD_USERNAME);
            }
        }
    }

    private void validateUsernameIsNotNull(String username) {
        if (username == null) {
            addError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_NULL, FIELD_USERNAME);
        }
    }

    private void validateUsernameHasMoreThanThreeCharacters(String username) {
        if (username != null && username.length() < USERNAME_MINIMUM_LENGTH) {
            addError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT, FIELD_USERNAME);
        }
    }

    private void validateUsernameHasLessThanTwentyCharacters(String username) {
        if (username != null && username.length() > USERNAME_MAXIMUM_LENGTH) {
            addError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG, FIELD_USERNAME);
        }
    }
    //endregion

    private void validatePassword(String username, String password) {
        validatePasswordNotNull(password);
        validatePasswordHasLesThanTwentyCharacters(password);
        validatePasswordHasMoreThanSixCharacters(password);
        validatePasswordIsDifferentFromUsername(username, password);
        validatePassWordHasNotInvalidCharacters(password);
    }

    private void validatePassWordHasNotInvalidCharacters(String password) {
        if (password != null) {
            Pattern passwordPattern = Pattern.compile(PASSWORD_PATTERN);
            boolean hasPasswordPattern = passwordPattern.matcher(password).matches();
            if (!hasPasswordPattern) {
                addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS, CreateUserValidator.FIELD_PASSWORD);
            }
        }
    }

    private void validatePasswordIsDifferentFromUsername(String username, String password) {
        if(username!=null && username.equals(password)){
            addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME, FIELD_PASSWORD);
        }
    }

    private void validatePasswordHasLesThanTwentyCharacters(String password) {
        if(password!=null && password.length()> PASSWORD_MAXIMUM_LENGTH){
            addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG, FIELD_PASSWORD);
        }
    }

    private void validatePasswordHasMoreThanSixCharacters(String password) {
        if(password!=null && password.length()< PASSWORD_MINIMUM_LENGTH){
            addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT, FIELD_PASSWORD);
        }
    }

    private void validatePasswordNotNull(String password) {
        if(password==null){
            addError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL, FIELD_PASSWORD);
        }
    }

    private void addError(String code, int field) {
        fieldValidationErrors.add(new FieldValidationError(code, field));
    }
}
