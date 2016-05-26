package com.shootr.mobile.domain.interactor.user;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.user.ShootrUserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

public class RemoveSessionDataInteractorTest {
  private RemoveSessionDataInteractor removeSessionDataInteractor;
  @Mock ShootrUserService shootrUserService;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    removeSessionDataInteractor =
        new RemoveSessionDataInteractor(interactorHandler, postExecutionThread, shootrUserService);
  }

  @Test
  public void shouldRemoveSessionInShootrUserService() throws Exception {
    removeSessionDataInteractor.removeData(callback, errorCallback);

    verify(shootrUserService).removeSessionData();
  }

  @Test
  public void shouldNotifyLoadedAfterRemovingSession() throws Exception {
    removeSessionDataInteractor.removeData(callback, errorCallback);

    InOrder inOrder = inOrder(shootrUserService, callback);
    inOrder.verify(shootrUserService).removeSessionData();
    inOrder.verify(callback).onCompleted();
  }

  @Test
  public void shouldNotifyErrorWhenServiceThrowsException() throws Exception {
    doThrow(new ServerCommunicationException(new Throwable())).when(shootrUserService).removeSessionData();

    removeSessionDataInteractor.removeData(callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
