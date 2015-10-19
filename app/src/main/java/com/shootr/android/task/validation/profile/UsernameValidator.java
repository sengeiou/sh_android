package com.shootr.android.task.validation.profile;

import com.shootr.android.domain.exception.ShootrError;
import com.shootr.android.task.validation.FieldValidationError;
import com.shootr.android.task.validation.FieldValidator;
import com.shootr.android.ui.model.UserModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator extends FieldValidator {

    public static final int MAX_LENGTH = 25;
    public static final int MIN_LENGHT = 3;
    private static final String USERNAME_FORMAT_REGEX = "^([_A-Za-z0-9])*$";

    private String username;

    public UsernameValidator(UserModel userModel) {
        username = userModel.getUsername();
    }

    @Override protected int getField() {
        return FieldValidationError.FIELD_USERNAME;
    }

    @Override protected void performValidations() {
        validateUsernameEmpty();
        validateUsernameTooShort();
        validateUsernameTooLong();
        validateUsernameInvalidCharacters();
    }

    private void validateUsernameEmpty() {
        if (username == null || username.isEmpty()) {
            addError(ShootrError.ERROR_CODE_USERNAME_NULL);
        }
    }

    private void validateUsernameTooShort() {
        if (username != null && username.length() < MIN_LENGHT) {
            addError(ShootrError.ERROR_CODE_USERNAME_TOO_SHORT);
        }
    }

    private void validateUsernameTooLong() {
        if (username != null && username.length() > MAX_LENGTH) {
            addError(ShootrError.ERROR_CODE_USERNAME_TOO_LONG);
        }
    }

    private void validateUsernameInvalidCharacters() {
        if (username != null) {
            Pattern pattern = Pattern.compile(USERNAME_FORMAT_REGEX);
            Matcher matcher = pattern.matcher(username);
            if (!matcher.matches()) {
                addError(ShootrError.ERROR_CODE_USERNAME_INVALID_CHARACTERS);
            }
        }
    }
}
