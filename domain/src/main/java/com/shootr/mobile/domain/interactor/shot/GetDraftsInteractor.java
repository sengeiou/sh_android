package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.service.QueueRepository;
import java.util.List;
import javax.inject.Inject;

public class GetDraftsInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final QueueRepository queueRepository;

  private Callback callback;

  @Inject public GetDraftsInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, QueueRepository queueRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.queueRepository = queueRepository;
  }

  public void loadDrafts(Callback callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    List<QueuedShot> failedShots = queueRepository.getFailedQueue();
    notifyLoaded(failedShots);
  }

  private void notifyLoaded(final List<QueuedShot> failedShots) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(failedShots);
      }
    });
  }

  public interface Callback {

    void onLoaded(List<QueuedShot> drafts);
  }
}
