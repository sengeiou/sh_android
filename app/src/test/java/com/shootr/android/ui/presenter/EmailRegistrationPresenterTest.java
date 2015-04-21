package com.shootr.android.ui.presenter;

import com.shootr.android.domain.interactor.user.CreateAccountInteractor;
import com.shootr.android.ui.views.EmailRegistrationView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailRegistrationPresenterTest {

    public static final String EMAIL_INVALID = "invalidemail";
    private static final String EMAIL_VALID = "email@domain.com";
    @Mock EmailRegistrationView emailRegistrationView;
    @Mock CreateAccountInteractor createAccountInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;

    private EmailRegistrationPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailRegistrationPresenter(createAccountInteractor, errorMessageFactory);
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

    @Test public void shouldAskForConfirmationIfEmailIsValid() throws Exception {
        when(emailRegistrationView.getEmail()).thenReturn(EMAIL_VALID);
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
}