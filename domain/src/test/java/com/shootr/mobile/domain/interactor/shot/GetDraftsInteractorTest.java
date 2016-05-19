package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.QueuedShot;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.service.ShotQueueRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetDraftsInteractorTest {

  private GetDraftsInteractor getDraftsInteractor;
  @Mock ShotQueueRepository shotQueueRepository;
  @Mock GetDraftsInteractor.Callback callback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    getDraftsInteractor =
        new GetDraftsInteractor(interactorHandler, postExecutionThread, shotQueueRepository);
  }

  @Test public void shouldGetShotsFromQueueRepository() throws Exception {
    getDraftsInteractor.loadDrafts(callback);

    verify(shotQueueRepository).getFailedShotQueue();
  }

  @Test public void shouldNotifyLoadedWhenGetShotFromQueueRepository() throws Exception {
    when(shotQueueRepository.getFailedShotQueue()).thenReturn(queuedShot());

    getDraftsInteractor.loadDrafts(callback);

    verify(callback).onLoaded(anyList());
  }

  @Test public void shouldNotifyLoadedWhenGetShotsFromQueueRepository() throws Exception {
    when(shotQueueRepository.getFailedShotQueue()).thenReturn(queuedShots());

    getDraftsInteractor.loadDrafts(callback);

    verify(callback).onLoaded(anyList());
  }

  private List<QueuedShot> queuedShots() {
    return Arrays.asList(new QueuedShot(), new QueuedShot());
  }

  private List<QueuedShot> queuedShot() {
    return Collections.singletonList(new QueuedShot());
  }
}
