package com.shootr.mobile.ui.presenter;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.user.ChangePasswordInteractor;
import com.shootr.mobile.domain.interactor.user.RemoveSessionDataInteractor;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.ui.views.ChangePasswordView;
import com.shootr.mobile.util.ErrorMessageFactory;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangePasswordPresenterTest {

  private static final String CURRENT_PASSWORD = "current";
  private static final String NEW_PASSWORD = "newPassword";
  private static final String USERNAME = "username";
  private static final String WRONG_PASSWORD = "bla";
  @Mock ErrorMessageFactory errorMessageFactory;
  @Mock ChangePasswordInteractor changePasswordInteractor;
  @Mock RemoveSessionDataInteractor removeSessionDataInteractor;
  @Mock SessionRepository sessionRepository;
  @Mock ChangePasswordView changePasswordView;

  private ChangePasswordPresenter presenter;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    presenter = new ChangePasswordPresenter(errorMessageFactory, changePasswordInteractor,
        removeSessionDataInteractor, sessionRepository);
    presenter.setView(changePasswordView);
  }

  @Test public void shouldShowLogoutInProgressWhenAttempToChangePassword() throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());
    setupChangePasswordInteractorCallback();

    presenter.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).showLogoutInProgress();
  }

  @Test public void shouldnavigateToWelcomeScreenWhenAttempToChangePassword() {
    when(sessionRepository.getCurrentUser()).thenReturn(user());
    setupChangePasswordInteractorCallback();
    setupRemoveSessionDataInteractor();

    presenter.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).navigateToWelcomeScreen();
  }

  @Test public void shouldHideLogoutInProgressWhenRemoveSessionDataInteractorThrowsError()
      throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());
    setupChangePasswordInteractorCallback();
    setupRemoveSessionDataInteractorErrorCallback();

    presenter.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).hideLogoutInProgress();
  }

  @Test public void shouldShowErrorInViewWhenChangePasswordInteractorThrowsError()
      throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());
    setupChangePasswordInteractorErrorCallback();

    presenter.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).showError(anyString());
  }

  @Test public void shouldShowCurrentPasswordErrorWhenCurrentPasswordIsNotValid() throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());

    presenter.attempToChangePassword(WRONG_PASSWORD, NEW_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).showCurrentPasswordError(anyString());
  }

  @Test public void shouldShowNewPasswordErrorWhenNewPasswordIsNotValid() throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());

    presenter.attempToChangePassword(CURRENT_PASSWORD, WRONG_PASSWORD, NEW_PASSWORD);

    verify(changePasswordView).showNewPasswordError(anyString());
  }

  @Test public void shouldShowNewPasswordAgainErrorWhenPasswordIsNotValid() throws Exception {
    when(sessionRepository.getCurrentUser()).thenReturn(user());

    presenter.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, WRONG_PASSWORD);

    verify(changePasswordView, atLeastOnce()).showNewPasswordAgainError(anyString());
  }

  private void setupChangePasswordInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[3];
        errorCallback.onError(any(ShootrException.class));
        return null;
      }
    }).when(changePasswordInteractor)
        .attempToChangePassword(anyString(), anyString(), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  private void setupRemoveSessionDataInteractorErrorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.ErrorCallback errorCallback =
            (Interactor.ErrorCallback) invocation.getArguments()[1];
        errorCallback.onError(new ShootrException() {
        });
        return null;
      }
    }).when(removeSessionDataInteractor)
        .removeData(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupRemoveSessionDataInteractor() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[0];
        completedCallback.onCompleted();
        return null;
      }
    }).when(removeSessionDataInteractor)
        .removeData(any(Interactor.CompletedCallback.class), any(Interactor.ErrorCallback.class));
  }

  private void setupChangePasswordInteractorCallback() {
    doAnswer(new Answer() {
      @Override public Object answer(InvocationOnMock invocation) throws Throwable {
        Interactor.CompletedCallback completedCallback =
            (Interactor.CompletedCallback) invocation.getArguments()[2];
        completedCallback.onCompleted();
        return null;
      }
    }).when(changePasswordInteractor)
        .attempToChangePassword(anyString(), anyString(), any(Interactor.CompletedCallback.class),
            any(Interactor.ErrorCallback.class));
  }

  private User user() {
    User user = new User();
    user.setUsername(USERNAME);
    return user;
  }
}
