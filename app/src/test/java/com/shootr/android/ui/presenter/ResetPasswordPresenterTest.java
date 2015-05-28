package com.shootr.android.ui.presenter;

import com.shootr.android.domain.exception.InvalidResetPasswordException;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ResetPasswordInteractor;
import com.shootr.android.ui.views.ResetPasswordView;
import com.shootr.android.util.ErrorMessageFactory;
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

public class ResetPasswordPresenterTest {

    private static final String USERNAME_STUB = "username";
    private static final String EMPTY_INPUT = "";

    @Mock ResetPasswordView resetPasswordView;
    @Mock ResetPasswordInteractor resetPasswordInteractor;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock Interactor.ErrorCallback errorCallback;

    private ResetPasswordPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        presenter = new ResetPasswordPresenter(resetPasswordInteractor, errorMessageFactory);
        presenter.setView(resetPasswordView);
    }

    @Test public void shouldShowResetButtonEnabledWhenUsernameInputHaveText() {
        when(resetPasswordView.getUsernameOrEmail()).thenReturn(USERNAME_STUB);
        presenter.inputTextChanged();
        verify(resetPasswordView).enableResetButton();
    }

    @Test public void shouldShowResetButtonDisabledWhenUsernameNotInInput() {
        when(resetPasswordView.getUsernameOrEmail()).thenReturn(EMPTY_INPUT);
        presenter.inputTextChanged();
        verify(resetPasswordView, never()).enableResetButton();
    }

    @Test public void shouldShowCredentialErrorWhenInteractorCallbacksResetException() throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new InvalidResetPasswordException("exception"));
                return null;
            }
        }).when(resetPasswordInteractor)
          .attempResetPassword(anyString(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));

        presenter.attempReset();

        verify(resetPasswordView).showError(anyString());
    }

    @Test public void shouldShowCommunicationErrorWhenInteractorCallbacksServerCommunicationException()
      throws Exception {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ServerCommunicationException(null));
                return null;
            }
        }).when(resetPasswordInteractor)
          .attempResetPassword(anyString(),
            any(Interactor.Callback.class),
            any(Interactor.ErrorCallback.class));

        presenter.attempReset();

        verify(resetPasswordView).showError(anyString());
    }

}
