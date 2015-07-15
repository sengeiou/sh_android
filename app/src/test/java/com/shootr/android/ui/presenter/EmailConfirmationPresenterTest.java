package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailConfirmationView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class EmailConfirmationPresenterTest {

    public static final String EMAIL = "email@email.com";
    public static final String INVALID_EMAIL = "invalid_email";

    @Mock EmailConfirmationView emailConfirmationView;
    @Mock ErrorMessageFactory errorMessageFactory;

    private EmailConfirmationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailConfirmationPresenter(errorMessageFactory);
        presenter.setView(emailConfirmationView);
    }

    @Test
    public void shouldShowAlertWhenPresenterInitialized() {
        presenter.initialize(emailConfirmationView, EMAIL);
        verify(emailConfirmationView).showConfirmationToUser(EMAIL);
    }

    @Test
    public void shouldShowUserEmailWhenPresenterInitialized() {
        presenter.initialize(emailConfirmationView, EMAIL);
        verify(emailConfirmationView).showUserEmail(EMAIL);
    }

    @Test
    public void shouldNotShowDoneButtonWhenEmailIsNotValid() {
        presenter.onEmailEdited(INVALID_EMAIL);
        verify(emailConfirmationView, never()).updateDoneButton();
    }

    @Test
    public void shouldShowDoneButtonWhenEmailIsValid() {
        presenter.onEmailEdited(EMAIL);
        verify(emailConfirmationView).updateDoneButton();
    }

    @Test
    public void shouldShowErrorWhenEmailIsNotValid() {
        presenter.onEmailEdited(INVALID_EMAIL);
        verify(emailConfirmationView).showEmailError(anyString());
    }

}
