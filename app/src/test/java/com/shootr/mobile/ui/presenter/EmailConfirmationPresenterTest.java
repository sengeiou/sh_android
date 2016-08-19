package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ChangeEmailInteractor;
import com.shootr.mobile.domain.interactor.user.ConfirmEmailInteractor;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.utils.StreamJoinDateFormatter;
import com.shootr.mobile.ui.model.mappers.UserModelMapper;
import com.shootr.mobile.ui.views.EmailConfirmationView;
import com.shootr.mobile.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmationPresenterTest {

    public static final String EMAIL = "email@email.com";
    public static final String INVALID_EMAIL = "invalid_email";
    public static final String ANOTHER_EMAIL = "another_email@gmail.com";
    public static final String EMPTY_EMAIL = "";

    @Mock EmailConfirmationView emailConfirmationView;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock ConfirmEmailInteractor confirmEmailInteractor;
    @Mock ChangeEmailInteractor changeEmailInteractor;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock SessionRepository sessionRepository;
    @Mock StreamJoinDateFormatter streamJoinDateFormatter;

    private EmailConfirmationPresenter presenter;

    @Before public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper(streamJoinDateFormatter);
        presenter = new EmailConfirmationPresenter(errorMessageFactory,
          confirmEmailInteractor,
          changeEmailInteractor,
          sessionRepository,
          userModelMapper);
        presenter.setView(emailConfirmationView);
    }

    @Test public void shouldShowAlertWhenPresenterInitializedAndEmailNotConfirmed() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());
        setupConfirmEmailCallbackCompleted();

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView).showConfirmationEmailSentAlert(anyString(), any(Runnable.class));
    }

    @Test public void shouldNotShowAlertWhenEmailConfirmed() {
        setupConfirmEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView, never()).showConfirmationEmailSentAlert(eq(EMAIL), any(Runnable.class));
    }

    @Test public void shouldShowErrorWhenPresenterInitializedAndNoConnection() {
        setupConfirmEmailErrorCallback();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView).showError(anyString());
    }

    @Test public void shouldShowUserEmailWhenPresenterInitialized() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView).showUserEmail(EMAIL);
    }

    @Test public void shouldNotShowDoneButtonWhenEmailIsNotValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(INVALID_EMAIL);

        verify(emailConfirmationView, never()).showDoneButton();
    }

    @Test public void shouldShowDoneButtonWhenEmailIsValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(EMAIL);

        verify(emailConfirmationView).showDoneButton();
    }

    @Test public void shouldShowErrorWhenEmailIsNotValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(INVALID_EMAIL);

        verify(emailConfirmationView, atLeastOnce()).showEmailError(anyString());
    }

    @Test public void shouldShowConfirmationWhenEmailIsValidAndDoneButtonPressed() {
        setupConfirmEmailCallbackCompleted();
        setupChangeEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.done(EMAIL);

        verify(emailConfirmationView).showConfirmationEmailSentAlert(anyString(), any(Runnable.class));
    }

    @Test public void shouldHideDoneButtonWhenEmailIsValidAndDoneButtonPressed() {
        setupConfirmEmailCallbackCompleted();
        setupChangeEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.done(EMAIL);

        verify(emailConfirmationView).showConfirmationEmailSentAlert(anyString(), any(Runnable.class));
    }

    @Test public void shouldShowAlertWhenEmailChangedAndIsValid() {
        setupChangeEmailCallbackCompleted();
        setupConfirmEmailCallbackCompleted();

        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.done(ANOTHER_EMAIL);

        verify(emailConfirmationView).showConfirmationEmailSentAlert(eq(ANOTHER_EMAIL), any(Runnable.class));
    }

    @Test public void shouldShowErrorWhenEmailChangedAndNoConnection() {
        setupChangeEmailInteractorErrorCallback();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.done(ANOTHER_EMAIL);

        verify(emailConfirmationView).showError(anyString());
    }

    @Test public void shouldNotShowAlertWhenEmailIsEmptyAndSocialLogin() throws Exception {
        setupConfirmEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmedAndSocialLogin());

        presenter.initialize(emailConfirmationView, EMPTY_EMAIL);

        verify(emailConfirmationView, never()).showConfirmationEmailSentAlert(eq(EMAIL), any(Runnable.class));
    }

    private void setupChangeEmailInteractorErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[2];
                errorCallback.onError(new ServerCommunicationException(new Throwable()));
                return null;
            }
        }).when(changeEmailInteractor)
          .changeEmail(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupConfirmEmailErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ServerCommunicationException(new Throwable()));
                return null;
            }
        }).when(confirmEmailInteractor)
          .confirmEmail(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupConfirmEmailCallbackCompleted() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(confirmEmailInteractor)
          .confirmEmail(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupChangeEmailCallbackCompleted() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback =
                  (Interactor.CompletedCallback) invocation.getArguments()[1];
                completedCallback.onCompleted();
                return null;
            }
        }).when(changeEmailInteractor)
          .changeEmail(anyString(), any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private User userWithoutEmailConfirmed() {
        User user = new User();
        user.setEmailConfirmed(false);
        return user;
    }

    private User userWithoutEmailConfirmedAndSocialLogin() {
        User user = new User();
        user.setEmailConfirmed(false);
        user.setSocialLogin(true);
        return user;
    }

    private User userWithEmailConfirmed() {
        User user = new User();
        user.setEmailConfirmed(true);
        return user;
    }
}
