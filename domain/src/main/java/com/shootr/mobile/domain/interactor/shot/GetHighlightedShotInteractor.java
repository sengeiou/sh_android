package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.HighlightedShot;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class GetHighlightedShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository localShotRepository;
  private final ShotRepository remoteShotRepository;

  private Callback<HighlightedShot> callback;
  private String streamId;

  @Inject public GetHighlightedShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShotRepository localShotRepository,
      @Remote ShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void loadHighlightedShots(String streamId, Callback<HighlightedShot> callback) {
    this.streamId = streamId;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      notifyLoaded(remoteShotRepository.getHighlightedShots(streamId));
    } catch (ServerCommunicationException error) {
      notifyLoaded(localShotRepository.getHighlightedShots(streamId));
    }
  }

  private void notifyLoaded(final HighlightedShot result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }

}
