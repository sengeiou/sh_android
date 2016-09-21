package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import javax.inject.Inject;

public class GetLocalHighlightedShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalShotRepository localShotRepository;

  private Callback<HighlightedShot> callback;
  private String streamId;

  @Inject public GetLocalHighlightedShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalShotRepository localShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
  }

  public void loadHighlightedShot(String streamId, Callback<HighlightedShot> callback) {
    this.streamId = streamId;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notifyLoaded(localShotRepository.getHighlightedShots(streamId));
  }

  private void notifyLoaded(final HighlightedShot result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }
}
