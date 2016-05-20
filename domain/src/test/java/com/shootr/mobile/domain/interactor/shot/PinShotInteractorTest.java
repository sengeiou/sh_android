package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;

public class PinShotInteractorTest {

  public static final String ID_SHOT = "idShot";
  private PinShotInteractor interactor;
  @Mock ShotRepository remoteShotRepository;
  @Mock Interactor.CompletedCallback completedCallback;
  @Mock PostExecutionThread postExecutionThread;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    TestInteractorHandler interactorHandler = new TestInteractorHandler();
    interactor =
        new PinShotInteractor(interactorHandler, postExecutionThread, remoteShotRepository);
  }

  @Test public void shouldSendUnhideShotToServer() throws Exception {
    interactor.pinShot(ID_SHOT, completedCallback);

    verify(remoteShotRepository).unhideShot(ID_SHOT);
  }

  @Test public void shouldNotifyCompletedWhenSendUnhideShotToServer() throws Exception {
    interactor.pinShot(ID_SHOT, completedCallback);

    InOrder inOrder = inOrder(remoteShotRepository, postExecutionThread);
    inOrder.verify(remoteShotRepository).unhideShot(anyString());
    inOrder.verify(postExecutionThread).post(any(Runnable.class));
  }
}
