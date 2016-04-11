package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.PerformEmailLoginInteractor;
import com.shootr.mobile.domain.service.user.LoginException;
import com.shootr.mobile.ui.views.EmailLoginView;
import com.shootr.mobile.util.ErrorMessageFactory;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailLoginPresenterTest {

    private static final String USERNAME_STUB = "username";
    private static final String PASSWORD_STUB = "password";
    private static final String EMPTY_INPUT = "";

    @Mock EmailLoginView emailLoginView;
    @Mock PerformEmailLoginInteractor emailLoginInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock Interactor.ErrorCallback errorCallback;

    private EmailLoginPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new EmailLoginPresenter(emailLoginInteractor, errorMessageFactory);
        presenter.setView(emailLoginView);
    }

    @Test public void shouldShowLoginButtonEnabledWhenEmailAndPasswordInputHaveText() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(USERNAME_STUB);
        when(emailLoginView.getPassword()).thenReturn(PASSWORD_STUB);
        presenter.inputTextChanged();
        verify(emailLoginView).enableLoginButton();
    }

    @Test public void shouldShowLoginButtonDisabledWhenUsernameNotInInput() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(EMPTY_INPUT);
        when(emailLoginView.getPassword()).thenReturn(PASSWORD_STUB);
        presenter.inputTextChanged();
        verify(emailLoginView, never()).enableLoginButton();
    }

    @Test public void shouldShowLoginButtonDisabledWhenPasswordNotInInput() {
        when(emailLoginView.getUsernameOrEmail()).thenReturn(USERNAME_STUB);
        when(emailLoginView.getPassword()).thenReturn(EMPTY_INPUT);
        presenter.inputTextChanged();
        verify(emailLoginView, never()).enableLoginButton();
    }

    @Test public void shouldShowCredentialErrorWhenInteractorCallbacksLoginException() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(new LoginException("test"));
                return null;
            }
        }).when(emailLoginInteractor)
          .attempLogin(anyString(),
            anyString(),
            any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));

        presenter.attempLogin();

        verify(emailLoginView).showError(anyString());
    }

    @Test public void shouldShowCommunicationErrorWhenInteractorCallbacksServerCommunicationException()
      throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(new ServerCommunicationException(null));
                return null;
            }
        }).when(emailLoginInteractor)
          .attempLogin(anyString(),
            anyString(),
            any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));

        presenter.attempLogin();

        verify(emailLoginView).showError(anyString());
    }

    @Test public void shouldCallShowErrorOnViewButNoEmailButtonPrintsCredentialsErrorOrEmailButtonPrintsCommunicationErrorWhenInteractorCallbacksShootrException()
      throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[3];
                errorCallback.onError(new ShootrException() {});

                return null;
            }
        }).when(emailLoginInteractor).attempLogin(anyString(),
          anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));

        presenter.attempLogin();

        verify(emailLoginView).showError(anyString());
    }

}
