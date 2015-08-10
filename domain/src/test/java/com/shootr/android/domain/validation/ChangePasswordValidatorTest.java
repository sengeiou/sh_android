package com.shootr.android.domain.validation;

import com.shootr.android.domain.exception.ShootrError;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangePasswordValidatorTest {

    private ChangePasswordValidator validator;
    private List<FieldValidationError> errors;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = new ChangePasswordValidator();
    }

    @Test public void shouldReturnPasswordEqualsCurrentPasswordIfPasswordAndNewPasswordAreEquals() {
        errors = validator.validate("password", "password", "password", "username");

        assertThat(errors).contains(newPasswordEqualsPassword());
    }

    @Test public void shouldReturnNewPasswordDifferentNewPasswordAgainIfBothPasswordsAreDifferent() {
        errors = validator.validate("password", "password", "password_again", "username");

        assertThat(errors).contains(newPasswordDifferentNewPasswordAgain());
    }

    @Test public void shouldReturnNewPasswordIsEqualsUsernameIfItsUsername() {
        errors = validator.validate("password", "username", "username", "username");

        assertThat(errors).contains(newPasswordEqualsUsername());
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

}
