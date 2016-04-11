package com.shootr.mobile.domain.validation;

import com.shootr.mobile.domain.exception.ShootrError;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordValidatorTest {

    public static final String PASSWORD = "password";
    public static final String USERNAME = "username";
    public static final String PASSWORD_AGAIN = "password_again";
    public static final String NEW_PASSWORD = "new_password";
    public static final String NEW_PASSWORD_WITH_MORE_THAN_20_CHARACTERS =
      "new_password_with_more_than_20_characters";
    public static final String NEW_PASSWORD_WITH_LESS_5_CHARACTERS = "12345";
    private ChangePasswordValidator validator;
    private List<FieldValidationError> errors;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = new ChangePasswordValidator();
    }

    @Test public void shouldReturnPasswordEqualsCurrentPasswordIfPasswordAndNewPasswordAreEquals() {
        errors = validator.validate(PASSWORD, PASSWORD, PASSWORD, USERNAME);

        assertThat(errors).contains(newPasswordEqualsPassword());
    }

    @Test public void shouldReturnNewPasswordDifferentNewPasswordAgainIfBothPasswordsAreDifferent() {
        errors = validator.validate(PASSWORD, PASSWORD, PASSWORD_AGAIN, USERNAME);

        assertThat(errors).contains(newPasswordDifferentNewPasswordAgain());
    }

    @Test public void shouldReturnNewPasswordIsEqualsUsernameIfItsUsername() {
        errors = validator.validate(PASSWORD, USERNAME, USERNAME, USERNAME);

        assertThat(errors).contains(newPasswordEqualsUsername());
    }

    @Test public void shouldReturnCurrentPasswordIsNullIfCurrentPasswordIsNull() {
        errors = validator.validate(null, PASSWORD, PASSWORD, USERNAME);

        assertThat(errors).contains(currentPasswordIsNull());
    }

    @Test public void shouldReturnNewPasswordIsNullIfNewPasswordIsNull() {
        errors = validator.validate(PASSWORD, null, NEW_PASSWORD, USERNAME);

        assertThat(errors).contains(newPasswordIsNull());
    }

    @Test public void shouldReturnNewPasswordAgainIsNullIfNewPasswordAgainIsNull() {
        errors = validator.validate(PASSWORD, NEW_PASSWORD, null, USERNAME);

        assertThat(errors).contains(newPasswordAgainIsNull());
    }

    @Test public void shouldReturnNewPasswordHasInvalidCharacters() {
        errors = validator.validate(PASSWORD, "new^password", "new^password", USERNAME);

        assertThat(errors).contains(newPasswordWithInvalidCharacters());
    }

    @Test public void shouldReturnNewPasswordHasMoreThan20Characters() {
        errors = validator.validate(PASSWORD,
          NEW_PASSWORD_WITH_MORE_THAN_20_CHARACTERS,
          NEW_PASSWORD_WITH_MORE_THAN_20_CHARACTERS,
          USERNAME);

        assertThat(errors).contains(newPasswordWithMoreThan20Characters());
    }

    @Test public void shouldReturnNewPasswordHasLessThan6Characters() {
        errors = validator.validate(PASSWORD,
          NEW_PASSWORD_WITH_LESS_5_CHARACTERS,
          NEW_PASSWORD_WITH_LESS_5_CHARACTERS,
          USERNAME);

        assertThat(errors).contains(newPasswordWithLessThan6Characters());
    }

    private FieldValidationError newPasswordEqualsPassword() {
        return new FieldValidationError(ShootrError.ERROR_NEW_PASSWORD_EQUALS_CURRENT_PASSWORD,
          ChangePasswordValidator.FIELD_NEW_PASSWORD_AGAIN);
    }

    private FieldValidationError newPasswordDifferentNewPasswordAgain() {
        return new FieldValidationError(ShootrError.ERROR_NEW_PASSWORD_NOT_EQUALS_NEW_PASSWORD_AGAIN,
          ChangePasswordValidator.FIELD_NEW_PASSWORD_AGAIN);
    }

    private FieldValidationError newPasswordEqualsUsername() {
        return new FieldValidationError(ShootrError.ERROR_CODE_NEW_PASSWORD_EQUALS_USERNAME,
          ChangePasswordValidator.FIELD_NEW_PASSWORD_AGAIN);
    }

    private FieldValidationError currentPasswordIsNull() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL,
          ChangePasswordValidator.FIELD_CURRENT_PASSWORD);
    }

    private FieldValidationError newPasswordIsNull() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL,
          ChangePasswordValidator.FIELD_NEW_PASSWORD);
    }

    private FieldValidationError newPasswordAgainIsNull() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL,
          ChangePasswordValidator.FIELD_NEW_PASSWORD_AGAIN);
    }

    private FieldValidationError newPasswordWithInvalidCharacters() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS,
          ChangePasswordValidator.FIELD_NEW_PASSWORD);
    }

    private FieldValidationError newPasswordWithMoreThan20Characters() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG,
          ChangePasswordValidator.FIELD_NEW_PASSWORD);
    }

    private FieldValidationError newPasswordWithLessThan6Characters() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT,
          ChangePasswordValidator.FIELD_NEW_PASSWORD);
    }
}
