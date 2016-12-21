package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.executor.TestPostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.interactor.TestInteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.service.QueueRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class DeleteDraftInteractorTest {

  public static final long QUEUED_SHOT_ID = 0L;
  @Mock QueueRepository queueRepository;
  private DeleteDraftInteractor deleteDraftInteractor;
  @Mock DeleteDraftInteractor.Callback callback;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    PostExecutionThread postExecutionThread = new TestPostExecutionThread();
    InteractorHandler interactorHandler = new TestInteractorHandler();
    deleteDraftInteractor =
        new DeleteDraftInteractor(interactorHandler, postExecutionThread, queueRepository);
  }

  @Test public void shouldGetShotFromShotQueueRepository() throws Exception {
    deleteDraftInteractor.deleteDraft(QUEUED_SHOT_ID, callback);

    verify(queueRepository).getQueue(QUEUED_SHOT_ID, QueueRepository.SHOT_TYPE);
  }

  @Test public void shouldRemoveShotFromShotQueueRepository() throws Exception {
    deleteDraftInteractor.deleteDraft(QUEUED_SHOT_ID, callback);

    verify(queueRepository).remove(any(QueuedShot.class));
  }

  @Test public void shouldNotifyDeleted() throws Exception {
    deleteDraftInteractor.deleteDraft(QUEUED_SHOT_ID, callback);

    verify(callback).onDeleted();
  }
}
