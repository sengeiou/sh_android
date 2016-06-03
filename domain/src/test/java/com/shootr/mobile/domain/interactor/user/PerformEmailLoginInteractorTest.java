package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidLoginException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class PerformEmailLoginInteractorTest {
  public static final String USERNAME_OR_EMIAL = "username_or_emial";
  public static final String PASSWORD = "password";
  private PerformEmailLoginInteractor performEmailLoginInteractor;
  @Mock ShootrUserService shootrUserService;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    performEmailLoginInteractor =
        new PerformEmailLoginInteractor(interactorHandler, postExecutionThread, shootrUserService);
  }

  @Test
  public void shouldPerformLogin() throws Exception {
    performEmailLoginInteractor.attempLogin(USERNAME_OR_EMIAL, PASSWORD, callback, errorCallback);

    verify(shootrUserService).performLogin(anyString(), anyString());
  }

  @Test
  public void shouldNotifyErrorWhenServiceThrowsInvalidLoginException() throws Exception {
    doThrow(new InvalidLoginException(new Throwable())).when(shootrUserService)
        .performLogin(USERNAME_OR_EMIAL, PASSWORD);

    performEmailLoginInteractor.attempLogin(USERNAME_OR_EMIAL, PASSWORD, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test
  public void shouldNotifyErrorWhenServiceThrowsUnknownError() throws Exception {
    doThrow(new ShootrException() {
    }).when(shootrUserService).performLogin(USERNAME_OR_EMIAL, PASSWORD);

    performEmailLoginInteractor.attempLogin(USERNAME_OR_EMIAL, PASSWORD, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
