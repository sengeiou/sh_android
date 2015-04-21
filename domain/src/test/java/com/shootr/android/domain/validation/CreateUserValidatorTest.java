package com.shootr.android.domain.validation;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrError;
import java.util.List;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateUserValidatorTest {

    //region Constants
    private static final String USERNAME_SMALLER_THAN_MINIMUM = "aa";
    private static final String USERNAME_NULL = null;
    private static final String USERNAME_LONGER_THAN_MAXIMUM = "IT_HAS_21_CHARACTERS_";
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
    //endregion

    //region Username
    @Test public void shouldReturnUsernameNullErrorIfUsernameIsNull() {
        User user = userWithNullUsername();

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(user, PASSWORD_STUB);

        assertThat(errors).contains(usernameIsNullError());
    }

    @Test public void shouldReturnUsernameTooShortErrorIfLessThanThreeCharacters() {
        User user = userWithShortUsername();

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(user, PASSWORD_STUB);

        assertThat(errors).contains(usernameTooShortError());
    }

    @Test public void shouldReturnusernameTooLongErrorIfMoreThanTwentyCharacters() {
        User user = userWithLongUsername();

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(user, PASSWORD_STUB);

        assertThat(errors).contains(usernameTooLongError());
    }

    @Test public void shouldReturnUsernameInvalidCharacterErrorIfUsernameHasInvalidCharacters() {
        User user = userWithInvalidCharactersInUsername();

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(user, PASSWORD_STUB);

        assertThat(errors).contains(usernameHasInvalidCharactersError());
    }
    //endregion

    //region Email
    @Test public void shouldReturnEmailInvalidFormatErrorIfEmailHasInvalidFormat() throws Exception {
        User user = userWithInvalidEmail();

        List<FieldValidationError> errors = new CreateUserValidator().validate(user, PASSWORD_STUB);

        assertThat(errors).contains(new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_INVALID_FORMAT,
          CreateUserValidator.FIELD_EMAIL));
    }

    @Test public void shouldReturnEmailNullErrorIfEmailIsNull() throws Exception {
        User user = userWithNullEmail();

        List<FieldValidationError> errors = new CreateUserValidator().validate(user, PASSWORD_STUB);

        assertThat(errors).contains(new FieldValidationError(ShootrError.ERROR_CODE_REGISTRATION_EMAIL_NULL,
          CreateUserValidator.FIELD_EMAIL));
    }
    //endregion

    //region Password
    @Test public void shouldReturnPasswordNullErrorIfPasswordIsNull() throws Exception {

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), PASSWORD_NULL);

        assertThat(errors).contains(passwordIsNullError());
    }

    @Test public void shouldReturnPasswordIsTooLongErrorIfMoreThanTwentyCharacters() throws Exception {

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), PASSWORD_TOO_LONG);

        assertThat(errors).contains(passwordTooLongError());
    }

    @Test public void shouldReturnPasswordIsTooShortErrorIfLessThanSixCharacters() throws Exception {

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), PASSWORD_TOO_SHORT);

        assertThat(errors).contains(passwordTooShortError());
    }

    @Test public void shouldReturnPasswordEqualsUsernameErrorIfIsEqualToUsername() throws Exception {

        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), USERNAME_STUB);

        assertThat(errors).contains(passwordIsEqualToUsernameError());
    }

    @Test public void shouldReturnPasswordHasNotValidCharactersErrorIfContainsInvalidCharacters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), PASSWORD_WITH_INVALID_CHARACTERS);

        assertThat(errors).contains(passwordHasInvalidCharactersError());
    }

    @Test public void shouldNotReturnPasswordInvalidErrorIfPasswordHasValidCharaters() throws Exception {
        CreateUserValidator validator = new CreateUserValidator();
        List<FieldValidationError> errors = validator.validate(userStub(), PASSWORD_VALID);

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

    //region User constructors
    private User userWithInvalidCharactersInUsername() {
        User user = new User();
        user.setUsername(USERNAME_WITH_INVALID_CHARACTERS);
        return user;
    }

    private User userStub() {
        User user = new User();
        user.setUsername(USERNAME_STUB);
        return user;
    }

    private User userWithNullEmail() {
        User user = new User();
        user.setEmail(EMAIL_NULL);
        return user;
    }

    private User userWithInvalidEmail() {
        User user = new User();
        user.setEmail(EMAIL_INVALID_FORMAT);
        return user;
    }

    private User userWithLongUsername() {
        User user = new User();
        user.setUsername(USERNAME_LONGER_THAN_MAXIMUM);
        return user;
    }

    private User userWithNullUsername() {
        User user = new User();
        user.setUsername(USERNAME_NULL);
        return user;
    }

    private User userWithShortUsername() {
        User user = new User();
        user.setUsername(USERNAME_SMALLER_THAN_MINIMUM);
        return user;
    }
    //endregion
}