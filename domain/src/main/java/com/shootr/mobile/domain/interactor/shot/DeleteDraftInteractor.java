package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.service.QueueRepository;
import javax.inject.Inject;

public class DeleteDraftInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final QueueRepository queueRepository;

  private Long queuedShotId;
  private Callback callback;

  @Inject public DeleteDraftInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, QueueRepository queueRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.queueRepository = queueRepository;
  }

  public void deleteDraft(Long queuedShotId, Callback callback) {
    this.queuedShotId = queuedShotId;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    QueuedShot queuedShot = queueRepository.getQueue(queuedShotId, QueueRepository.SHOT_TYPE);
    queueRepository.remove(queuedShot);
    notifyDeleted();
  }

  private void notifyDeleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onDeleted();
      }
    });
  }

  public interface Callback {

    void onDeleted();
  }
}
