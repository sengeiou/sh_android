package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.repository.ShotRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

public class DeleteLocalShotsByStreamInteractorTest {

  public static final String ID_STREAM = "id_stream";
  private DeleteLocalShotsByStreamInteractor deleteLocalShotsByStreamInteractor;
  @Mock ShotRepository localShotRepository;
  @Mock Interactor.CompletedCallback callback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    deleteLocalShotsByStreamInteractor =
        new DeleteLocalShotsByStreamInteractor(localShotRepository, postExecutionThread,
            interactorHandler);
  }

  @Test public void shouldDeleteShotsInLocal() throws Exception {
    deleteLocalShotsByStreamInteractor.deleteShot(ID_STREAM, callback);

    verify(localShotRepository).deleteShotsByStream(ID_STREAM);
  }

  @Test public void shouldCompleteCallbackWhenDeletedShots() throws Exception {
    deleteLocalShotsByStreamInteractor.deleteShot(ID_STREAM, callback);

    verify(callback).onCompleted();
  }
}
