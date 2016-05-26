package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.EmailAlreadyConfirmedException;
import com.shootr.mobile.domain.exception.EmailAlreadyExistsException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.UnauthorizedRequestException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.EmailInUseException;
import com.shootr.mobile.domain.service.EmailIsAuthenticatedException;
import com.shootr.mobile.domain.service.InsufficientAuthenticationException;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ChangeEmailInteractorTest {

  private static final String EMAIL = "email";
  @Mock InteractorHandler interactorHandler;
  @Mock ShootrUserService shootrUserService;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private ChangeEmailInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    interactor =
        new ChangeEmailInteractor(interactorHandler, postExecutionThread, shootrUserService);
  }

  @Test public void shouldNotifyLoadedWhenChangeEmail() throws Exception {
    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotiftyErrorWhenChangeEmailAndThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(shootrUserService).changeEmail(anyString());

    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }

  @Test public void shouldNotiftyErrorWhenChangeEmailAndThrowsEmailAlreadyExistsException()
      throws Exception {
    doThrow(EmailAlreadyExistsException.class).when(shootrUserService).changeEmail(anyString());

    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(errorCallback).onError(any(EmailInUseException.class));
  }

  @Test public void shouldNotiftyErrorWhenChangeEmailAndThrowsUnauthorizedRequestException()
      throws Exception {
    doThrow(UnauthorizedRequestException.class).when(shootrUserService).changeEmail(anyString());

    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(errorCallback).onError(any(InsufficientAuthenticationException.class));
  }

  @Test public void shouldNotiftyErrorWhenChangeEmailAndThrowsEmailAlreadyConfirmedException()
      throws Exception {
    doThrow(EmailAlreadyConfirmedException.class).when(shootrUserService).changeEmail(anyString());

    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(errorCallback).onError(any(EmailIsAuthenticatedException.class));
  }

  @Test public void shouldNotiftyErrorWhenChangeEmailAndThrowsNullPointerException()
      throws Exception {
    doThrow(NullPointerException.class).when(shootrUserService).changeEmail(anyString());

    interactor.changeEmail(EMAIL, callback, errorCallback);

    verify(errorCallback).onError(any(InsufficientAuthenticationException.class));
  }
}
