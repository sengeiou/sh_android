package com.shootr.mobile.domain.validation;

import com.shootr.mobile.domain.exception.ShootrError;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserValidatorTest {

    public static final String EMAIL_WITH_PLUS_SIGN = "email+plus@domain.com";
    //region Constants
    private static final String USERNAME_SMALLER_THAN_MINIMUM = "aa";
    private static final String USERNAME_NULL = null;
    private static final String USERNAME_LONGER_THAN_MAXIMUM = "IT_HAS_26_CHARACTERS_ASDFG";
    private static final String EMAIL_INVALID_FORMAT = "invalidemail";
    private static final String EMAIL_NULL = null;
    private static final String PASSWORD_NULL = null;
    private static final String PASSWORD_STUB = "password";
    private static final String PASSWORD_TOO_LONG = "password_has_21_char_";
    private static final String PASSWORD_TOO_SHORT = "pass_";
    private static final String USERNAME_STUB = "john_doe";
    private static final String USERNAME_WITH_INVALID_CHARACTERS = "Us er ñame ¬~#@|(/&%$·";
    private static final String PASSWORD_WITH_INVALID_CHARACTERS = "pass ¬~#@|(/&%$·";
    private static final String PASSWORD_VALID = "123456";
    private static final String EMAIL_STUB = "test@test.com";
    //endregion

    //region Username
    @Test public void shouldReturnUsernameNullErrorIfUsernameIsNull() {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_NULL, PASSWORD_STUB);

        assertThat(errors).contains(usernameIsNullError());
    }

    @Test public void shouldReturnUsernameTooShortErrorIfLessThanThreeCharacters() {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors =
          validator.validate(EMAIL_STUB, USERNAME_SMALLER_THAN_MINIMUM, PASSWORD_STUB);

        assertThat(errors).contains(usernameTooShortError());
    }

    @Test public void shouldReturnusernameTooLongErrorIfMoreThanTwentyCharacters() {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_LONGER_THAN_MAXIMUM, PASSWORD_STUB);

        assertThat(errors).contains(usernameTooLongError());
    }

    @Test public void shouldReturnUsernameInvalidCharacterErrorIfUsernameHasInvalidCharacters() {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors =
          validator.validate(EMAIL_STUB, USERNAME_WITH_INVALID_CHARACTERS, PASSWORD_STUB);

        assertThat(errors).contains(usernameHasInvalidCharactersError());
    }
    //endregion

    //region Email
    @Test public void shouldReturnEmailInvalidFormatErrorIfEmailHasInvalidFormat() throws Exception {
        List<FieldValidationError> errors =
          new CreateUserValidator().validate(EMAIL_INVALID_FORMAT, USERNAME_STUB, PASSWORD_STUB);

        assertThat(errors).contains(new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT,
          CreateUserValidator.FIELD_EMAIL));
    }

    @Test public void shouldReturnEmailNullErrorIfEmailIsNull() throws Exception {
        List<FieldValidationError> errors =
          new CreateUserValidator().validate(EMAIL_NULL, USERNAME_STUB, PASSWORD_STUB);

        assertThat(errors).contains(new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL,
          CreateUserValidator.FIELD_EMAIL));
    }

    @Test public void shouldNotReturnEmailInvalidErrorIfEmailContainsPlusSign() throws Exception {
        List<FieldValidationError> errors =
          new CreateUserValidator().validate(EMAIL_WITH_PLUS_SIGN, USERNAME_STUB, PASSWORD_STUB);

        assertThat(errors).doesNotContain(new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT,
          CreateUserValidator.FIELD_EMAIL));
    }

    //endregion

    //region Password
    @Test public void shouldReturnPasswordNullErrorIfPasswordIsNull() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_STUB, PASSWORD_NULL);

        assertThat(errors).contains(passwordIsNullError());
    }

    @Test public void shouldReturnPasswordIsTooLongErrorIfMoreThanTwentyCharacters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_STUB, PASSWORD_TOO_LONG);

        assertThat(errors).contains(passwordTooLongError());
    }

    @Test public void shouldReturnPasswordIsTooShortErrorIfLessThanSixCharacters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_STUB, PASSWORD_TOO_SHORT);

        assertThat(errors).contains(passwordTooShortError());
    }

    @Test public void shouldReturnPasswordEqualsUsernameErrorIfIsEqualToUsername() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_STUB, USERNAME_STUB);

        assertThat(errors).contains(passwordIsEqualToUsernameError());
    }

    @Test public void shouldReturnPasswordHasNotValidCharactersErrorIfContainsInvalidCharacters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors =
          validator.validate(EMAIL_STUB, USERNAME_STUB, PASSWORD_WITH_INVALID_CHARACTERS);

        assertThat(errors).contains(passwordHasInvalidCharactersError());
    }

    @Test public void shouldNotReturnPasswordInvalidErrorIfPasswordHasValidCharaters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(EMAIL_STUB, USERNAME_STUB, PASSWORD_VALID);

        assertThat(errors).doesNotContain(passwordHasInvalidCharactersError());
    }

    //endregion

    //region Expected error constructors
    private FieldValidationError usernameHasInvalidCharactersError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_INVALID_CHARACTERS,
          CreateUserValidator.FIELD_USERNAME);
    }

    private FieldValidationError passwordHasInvalidCharactersError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_INVALID_CHARACTERS,
          CreateUserValidator.FIELD_PASSWORD);
    }

    private FieldValidationError passwordIsEqualToUsernameError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_EQUALS_USERNAME,
          CreateUserValidator.FIELD_PASSWORD);
    }

    private FieldValidationError passwordTooShortError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_SHORT,
          CreateUserValidator.FIELD_PASSWORD);
    }

    private FieldValidationError passwordTooLongError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_TOO_LONG,
          CreateUserValidator.FIELD_PASSWORD);
    }

    private FieldValidationError passwordIsNullError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_PASSWORD_NULL,
          CreateUserValidator.FIELD_PASSWORD);
    }

    private FieldValidationError usernameTooLongError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_LONG,
          CreateUserValidator.FIELD_USERNAME);
    }

    private FieldValidationError usernameIsNullError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_NULL,
          CreateUserValidator.FIELD_USERNAME);
    }

    private FieldValidationError usernameTooShortError() {
        return new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_USERNAME_TOO_SHORT,
          CreateUserValidator.FIELD_USERNAME);
    }
    //endregion
}