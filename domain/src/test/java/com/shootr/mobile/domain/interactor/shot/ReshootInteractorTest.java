package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class ReshootInteractorTest {

  public static final String ID_SHOT = "id_shot";
  private ReshootInteractor reshootInteractor;
  @Mock ExternalShotRepository remoteShotRepository;
  @Mock InternalShotRepository internalShotRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    InteractorHandler interactorHandler = new TestInteractorHandler();
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    reshootInteractor =
        new ReshootInteractor(remoteShotRepository, internalShotRepository, interactorHandler, postExecutionThread);
  }

  @Test public void shouldShareShotInRemoteRepository() throws Exception {
    reshootInteractor.reshoot(ID_SHOT, callback, errorCallback);

    verify(remoteShotRepository).reshoot(anyString());
  }

  @Test public void shouldNotifyErrorWhenRemoteRepositoryThrowsShootrException() throws Exception {
    doThrow(new ShootrException() {
    }).when(remoteShotRepository).reshoot(anyString());

    reshootInteractor.reshoot(ID_SHOT, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }

  @Test public void shouldNotifyCompletedWhenShareShotInRemoteRepository() throws Exception {
    reshootInteractor.reshoot(ID_SHOT, callback, errorCallback);

    verify(callback).onCompleted();
  }
}
