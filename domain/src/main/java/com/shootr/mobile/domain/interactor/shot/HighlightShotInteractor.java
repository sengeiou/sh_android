package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import javax.inject.Inject;

public class HighlightShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;

  private String idShot;
  private CompletedCallback completedCallback;

  @Inject public HighlightShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void highlight(String idShot, CompletedCallback completedCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      remoteShotRepository.highlightShot(idShot);
    } catch (ServerCommunicationException e) {
      /* Ignore error and notify callback */
    }
    notifyCompleted();
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
