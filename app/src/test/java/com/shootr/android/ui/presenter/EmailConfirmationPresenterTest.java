package com.shootr.android.ui.presenter;

import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ServerCommunicationException;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.user.ChangeEmailInteractor;
import com.shootr.android.domain.interactor.user.ConfirmEmailInteractor;
import com.shootr.android.domain.interactor.user.UpdateUserInteractor;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.ui.model.mappers.UserModelMapper;
import com.shootr.android.ui.views.EmailConfirmationView;
import com.shootr.android.util.ErrorMessageFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailConfirmationPresenterTest {

    public static final String EMAIL = "email@email.com";
    public static final String INVALID_EMAIL = "invalid_email";
    public static final String ANOTHER_EMAIL = "another_email@gmail.com";

    @Mock EmailConfirmationView emailConfirmationView;
    @Mock ErrorMessageFactory errorMessageFactory;
    @Mock ConfirmEmailInteractor confirmEmailInteractor;
    @Mock ChangeEmailInteractor changeEmailInteractor;
    @Mock UpdateUserInteractor updateUserInteractor;
    @Mock Interactor.CompletedCallback completedCallback;
    @Mock Interactor.ErrorCallback errorCallback;
    @Mock SessionRepository sessionRepository;

    private EmailConfirmationPresenter presenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        UserModelMapper userModelMapper = new UserModelMapper();
        presenter = new EmailConfirmationPresenter(errorMessageFactory, confirmEmailInteractor, changeEmailInteractor,
          updateUserInteractor,
          sessionRepository, userModelMapper);
        presenter.setView(emailConfirmationView);
    }

    @Test
    public void shouldShowAlertWhenPresenterInitializedAndEmailNotConfirmed() {
        setupUpdateUserCompletedCallback();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());
        setupConfirmEmailCallbackCompleted();

        presenter.confirmEmail(EMAIL);

        verify(emailConfirmationView).showConfirmationToUser(anyString());
    }

    @Test
    public void shouldNotShowAlertWhenEmailConfirmed() {
        setupConfirmEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView, never()).showConfirmationToUser(EMAIL);
    }

    @Test
    public void shouldShowErrorWhenPresenterInitializedAndNoConnection() {
        setupUpdateUserErrorCallback();
        setupConfirmEmailErrorCallback();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView).showError(anyString());
    }

    @Test
    public void shouldShowUserEmailWhenPresenterInitialized() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.initialize(emailConfirmationView, EMAIL);

        verify(emailConfirmationView).showUserEmail(EMAIL);
    }

    @Test
    public void shouldNotShowDoneButtonWhenEmailIsNotValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(INVALID_EMAIL);

        verify(emailConfirmationView, never()).updateDoneButton();
    }

    @Test
    public void shouldShowDoneButtonWhenEmailIsValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(EMAIL);

        verify(emailConfirmationView).updateDoneButton();
    }

    @Test
    public void shouldShowErrorWhenEmailIsNotValid() {
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.onEmailEdited(INVALID_EMAIL);

        verify(emailConfirmationView, atLeastOnce()).showEmailError(anyString());
    }

    @Test
    public void shouldShowConfirmationWhenEmailIsValidAndDoneButtonPressed() {
        setupUpdateUserCompletedCallback();
        setupConfirmEmailCallbackCompleted();
        setupChangeEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.attempToChangeEmail(EMAIL);

        verify(emailConfirmationView).showConfirmationToUser(anyString());
    }

    @Test
    public void shouldHideDoneButtonWhenEmailIsValidAndDoneButtonPressed() {
        setupUpdateUserCompletedCallback();
        setupConfirmEmailCallbackCompleted();
        setupChangeEmailCallbackCompleted();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.attempToChangeEmail(EMAIL);

        verify(emailConfirmationView).showConfirmationToUser(anyString());
    }

    @Test
    public void shouldShowAlertWhenEmailChangedAndIsValid() {
        setupUpdateUserCompletedCallback();
        setupChangeEmailCallbackCompleted();
        setupConfirmEmailCallbackCompleted();


        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.attempToChangeEmail(ANOTHER_EMAIL);

        verify(emailConfirmationView).showConfirmationToUser(ANOTHER_EMAIL);
    }

    @Test
    public void shouldShowErrorWhenEmailChangedAndNoConnection() {
        setupoChangeEmailInteractorErrorCallback();
        when(sessionRepository.getCurrentUser()).thenReturn(userWithoutEmailConfirmed());

        presenter.attempToChangeEmail(ANOTHER_EMAIL);

        verify(emailConfirmationView).showError(anyString());
    }

    private void setupUpdateUserCompletedCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(updateUserInteractor)
          .updateCurrentUser(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupUpdateUserErrorCallback() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.ErrorCallback errorCallback = (Interactor.ErrorCallback) invocation.getArguments()[1];
                errorCallback.onError(new ServerCommunicationException(new Throwable()));
                return null;
            }
        }).when(updateUserInteractor)
          .updateCurrentUser(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
    }

    private void setupoChangeEmailInteractorErrorCallback() {
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
        }).when(confirmEmailInteractor).confirmEmail(any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupConfirmEmailCallbackCompleted() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[0];
                completedCallback.onCompleted();
                return null;
            }
        }).when(confirmEmailInteractor).confirmEmail(any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private void setupChangeEmailCallbackCompleted() {
        doAnswer(new Answer() {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable {
                Interactor.CompletedCallback completedCallback = (Interactor.CompletedCallback) invocation.getArguments()[1];
                completedCallback.onCompleted();
                return null;
            }
        }).when(changeEmailInteractor).changeEmail(anyString(),
          any(Interactor.CompletedCallback.class),
          any(Interactor.ErrorCallback.class));
    }

    private User userWithoutEmailConfirmed() {
        User user = new User();
        user.setEmailConfirmed(0);
        return user;
    }

    private User userWithEmailConfirmed() {
        User user = new User();
        user.setEmailConfirmed(1);
        return user;
    }

}
