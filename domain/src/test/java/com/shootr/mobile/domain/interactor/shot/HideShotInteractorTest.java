package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

public class HideShotInteractorTest {

  private static final String SHOT_ID = "shot_id";
  private HideShotInteractor hideShotInteractor;
  @Mock ShotRepository remoteShotRepository;
  @Mock Interactor.CompletedCallback callback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    TestPostExecutionThread postExecutionThread = new TestPostExecutionThread();
    hideShotInteractor =
        new HideShotInteractor(interactorHandler, postExecutionThread, remoteShotRepository);
  }

  @Test public void shouldNotifyCallbackAfterSendingToServer() throws Exception {
    hideShotInteractor.hideShot(SHOT_ID, callback);

    InOrder inOrder = inOrder(callback, remoteShotRepository);
    inOrder.verify(remoteShotRepository).hideShot(anyString(), anyLong());
    inOrder.verify(callback).onCompleted();
  }

  @Test public void shouldNotifyCallbackWhenServiceFails() throws Exception {
    doThrow(new ServerCommunicationException(null)).when(remoteShotRepository)
        .hideShot(anyString(), anyLong());

    hideShotInteractor.hideShot(SHOT_ID, callback);

    verify(callback).onCompleted();
  }

  @Test public void shouldSendHideShotToServer() throws Exception {
    hideShotInteractor.hideShot(SHOT_ID, callback);

    verify(remoteShotRepository).hideShot(anyString(), anyLong());
  }
}
