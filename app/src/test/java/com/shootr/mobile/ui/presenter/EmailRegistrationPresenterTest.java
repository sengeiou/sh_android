package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.interactor.user.CreateAccountInteractor;
import com.shootr.mobile.ui.views.EmailRegistrationView;
import com.shootr.mobile.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailRegistrationPresenterTest {

    public static final String EMAIL_INVALID = "invalidemail";
    private static final String EMAIL_VALID = "email@domain.com";
    private static final String PASSWORD_VALID = "123456";
    private static final String USERNAME_VALID = "username";
    private static final String USERNAME_INVALID = "user name invalid";
    private static final String PASSWORD_INVALID = "p·¬ssword";

    @Mock EmailRegistrationView emailRegistrationView;
    @Mock CreateAccountInteractor createAccountInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private EmailRegistrationPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailRegistrationPresenter(createAccountInteractor, errorMessageFactory);
        presenter.setView(emailRegistrationView);
    }

    @Test public void shouldShowViewLoadingWhenConfirmedCreateAccount() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.confirmAccountCreation();

        verify(emailRegistrationView).showLoading();
    }

    @Test public void shouldHideCreateButtonWhenConfirmedCreateAccount() throws Exception {
        presenter.setView(emailRegistrationView);

        presenter.confirmAccountCreation();

        verify(emailRegistrationView).hideCreateButton();
    }

    @Test public void shouldAskForConfirmationIfEmailUsernameAndPasswordAreValid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_VALID);
        when(emailRegistrationView.getUsername()).thenReturn(USERNAME_VALID);
        when(emailRegistrationView.getPassword()).thenReturn(PASSWORD_VALID);
        presenter.setView(emailRegistrationView);

        presenter.createAccount();

        verify(emailRegistrationView).askEmailConfirmation();
    }

    @Test public void shouldNotAskForConfirmationIfEmailIsInvalid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_INVALID);
        presenter.setView(emailRegistrationView);

        presenter.createAccount();

        verify(emailRegistrationView, never()).askEmailConfirmation();
    }

    //region Email validation
    @Test public void shouldShowEmailErrorWhenFocusRemovedIfEmailInvalid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_INVALID);

        presenter.emailFocusRemoved();

        verify(emailRegistrationView).showEmailError(anyString());
    }

    @Test
    public void shouldNotRequestEmailFocusWhenFocusRemovedIfEmailInvalid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_INVALID);

        presenter.emailFocusRemoved();

        verify(emailRegistrationView, never()).focusOnEmailField();
    }

    @Test
    public void shouldShowEmailErrorWhenCreateAccountIfEmailInvalid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).showEmailError(anyString());
    }

    @Test
    public void shouldRequestEmailFocusWhenCreateAccountIfEmailInvalid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).focusOnEmailField();
    }
    //endregion

    //region Username validation
    @Test public void shouldShowUsernameErrorWhenFocusRemovedIfUsernameInvalid() throws Exception {
        when(emailRegistrationView.getUsername()).thenReturn(USERNAME_INVALID);

        presenter.usernameFocusRemoved();

        verify(emailRegistrationView).showUsernameError(anyString());
    }

    @Test
    public void shouldNotRequestUsernameFocusWhenFocusRemovedIfUsernameInvalid() throws Exception {
        when(emailRegistrationView.getUsername()).thenReturn(USERNAME_INVALID);

        presenter.usernameFocusRemoved();

        verify(emailRegistrationView, never()).focusOnUsernameField();
    }

    @Test
    public void shouldShowUsernameErrorWhenCreateAccountIfUsernameInvalid() throws Exception {
        when(emailRegistrationView.getUsername()).thenReturn(USERNAME_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).showUsernameError(anyString());
    }

    @Test
    public void shouldRequestUsernameFocusWhenCreateAccountIfUsernameInvalid() throws Exception {
        when(emailRegistrationView.getUsername()).thenReturn(USERNAME_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).focusOnUsernameField();
    }
    //endregion

    //region Password validation
    @Test public void shouldShowPasswordErrorWhenFocusRemovedIfPasswordInvalid() throws Exception {
        when(emailRegistrationView.getPassword()).thenReturn(PASSWORD_INVALID);

        presenter.passwordFocusRemoved();

        verify(emailRegistrationView).showPasswordError(anyString());
    }

    @Test
    public void shouldNotRequestPasswordFocusWhenFocusRemovedIfPasswordInvalid() throws Exception {
        when(emailRegistrationView.getPassword()).thenReturn(PASSWORD_INVALID);

        presenter.passwordFocusRemoved();

        verify(emailRegistrationView, never()).focusOnPasswordField();
    }

    @Test
    public void shouldShowPasswordErrorWhenCreateAccountIfPasswordInvalid() throws Exception {
        when(emailRegistrationView.getPassword()).thenReturn(PASSWORD_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).showPasswordError(anyString());
    }

    @Test
    public void shouldRequestPasswordFocusWhenCreateAccountIfPasswordInvalid() throws Exception {
        when(emailRegistrationView.getPassword()).thenReturn(PASSWORD_INVALID);

        presenter.createAccount();

        verify(emailRegistrationView).focusOnPasswordField();
    }
    //endregion
}