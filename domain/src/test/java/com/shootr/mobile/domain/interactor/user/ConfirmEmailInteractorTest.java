package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ConfirmEmailInteractorTest {

  @Mock ShootrUserService shootrUserService;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  private ConfirmEmailInteractor interactor;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();

    interactor =
        new ConfirmEmailInteractor(interactorHandler, postExecutionThread, shootrUserService);
  }

  @Test public void shouldNotiftyLoadedWhenConfirmEmail() throws Exception {
    interactor.confirmEmail(callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenConfirmEmailANdThrowsServerComunicationException()
      throws Exception {
    doThrow(ServerCommunicationException.class).when(shootrUserService).requestEmailConfirmation();

    interactor.confirmEmail(callback, errorCallback);

    verify(errorCallback).onError(any(ServerCommunicationException.class));
  }
}
