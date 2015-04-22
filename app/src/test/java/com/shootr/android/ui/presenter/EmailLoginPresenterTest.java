package com.shootr.android.ui.presenter;

import com.shootr.android.ui.views.EmailLoginView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailLoginPresenterTest {

    private static final String USERNAME_STUB = "username";
    private static final String PASSWORD_STUB = "password";
    private static final String EMPTY_INPUT_STUB = "";

    @Mock EmailLoginView emailLoginView;

    private EmailLoginPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailLoginPresenter();
        presenter.setView(emailLoginView);
    }

    @Test public void shouldShowLoginButtonEnabledWhenAllInput() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(USERNAME_STUB);
        when(emailLoginView.getPassword()).thenReturn(PASSWORD_STUB);
        presenter.inputTextChanged();
        verify(emailLoginView).emailButtonIsEnabled();
    }

    @Test public void shouldShowLoginButtonDisabledWhenUsernameNotInInput() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(EMPTY_INPUT_STUB);
        when(emailLoginView.getPassword()).thenReturn(PASSWORD_STUB);
        presenter.inputTextChanged();
        verify(emailLoginView, never()).emailButtonIsEnabled();
    }

    @Test public void shouldShowLoginButtonDisabledWhenPasswordNotInInput() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(USERNAME_STUB);
        when(emailLoginView.getPassword()).thenReturn(EMPTY_INPUT_STUB);
        presenter.inputTextChanged();
        verify(emailLoginView, never()).emailButtonIsEnabled();
    }

}
