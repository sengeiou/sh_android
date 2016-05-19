package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

public class DeleteShotInteractorTest {

  public static final String ID_SHOT = "id_shot";
  private DeleteShotInteractor deleteShotInteractor;
  @Mock ShotRepository localShotRepository;
  @Mock ShotRepository remoteShotRepository;
  @Mock ActivityRepository localActivityRepository;
  @Mock Interactor.CompletedCallback callback;
  @Mock Interactor.ErrorCallback errorCallback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    deleteShotInteractor =
        new DeleteShotInteractor(localShotRepository, remoteShotRepository, localActivityRepository,
            postExecutionThread, interactorHandler);
  }

  @Test public void shouldDeleteShotInLocal() throws Exception {
    deleteShotInteractor.deleteShot(ID_SHOT, callback, errorCallback);

    verify(localShotRepository).deleteShot(anyString());
  }

  @Test public void shouldDeleteShotInRemote() throws Exception {
    deleteShotInteractor.deleteShot(ID_SHOT, callback, errorCallback);

    verify(remoteShotRepository).deleteShot(anyString());
  }

  @Test public void shouldDeleteLocalActivitiesRelatedToShot() throws Exception {
    deleteShotInteractor.deleteShot(ID_SHOT, callback, errorCallback);

    verify(localActivityRepository).deleteActivitiesWithShot(anyString());
  }

  @Test public void shouldNotifyLoadedWhenNoExceptionThrowed() throws Exception {
    deleteShotInteractor.deleteShot(ID_SHOT, callback, errorCallback);

    verify(callback).onCompleted();
  }

  @Test public void shouldNotifyErrorWhenServerCommunicationExceptionThrown() throws Exception {
    doThrow(new ServerCommunicationException(new Throwable())).when(remoteShotRepository)
        .deleteShot(anyString());

    deleteShotInteractor.deleteShot(ID_SHOT, callback, errorCallback);

    verify(errorCallback).onError(any(ShootrException.class));
  }
}
