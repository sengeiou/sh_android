package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.InvalidPasswordException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.ChangePasswordInvalidException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import com.shootr.mobile.domain.utils.LocaleProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ChangePasswordInteractorTest {

  private static final String CURRENT_PASSWORD = "currentPassword";
  private static final String NEW_PASSWORD = "newPassword";
  @Mock ShootrUserService shootrUserService;
  @Mock Interactor.ErrorCallback errorCallback;
  @Mock Interactor.CompletedCallback completedCallback;
  @Mock LocaleProvider localeProvider;

  private ChangePasswordInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new ChangePasswordInteractor(interactorHandler, postExecutionThread, shootrUserService,
            localeProvider);
  }

  @Test public void shouldNotifyLoadedWhenAttempToChangePassword() throws Exception {
    interactor.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, completedCallback,
        errorCallback);

    verify(completedCallback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenThrowsServerComunicationException() throws Exception {
    doThrow(ServerCommunicationException.class).when(shootrUserService)
        .changePassword(anyString(), anyString(), anyString());

    interactor.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, completedCallback,
        errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  @Test public void shouldNotifyErrorWhenThrowsInvalidPasswordException() throws Exception {
    doThrow(InvalidPasswordException.class).when(shootrUserService)
        .changePassword(anyString(), anyString(), anyString());

    interactor.attempToChangePassword(CURRENT_PASSWORD, NEW_PASSWORD, completedCallback,
        errorCallback);

    verify(errorCallback).onError(any(ChangePasswordInvalidException.class));
  }
}
