package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class DismissHighlightShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository remoteShotRepository;
  private final ShotRepository localShotRepository;

  private String idHighlightedShot;
  private Boolean isAdmin;
  private CompletedCallback completedCallback;

  @Inject public DismissHighlightShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote ShotRepository remoteShotRepository,
      @Local ShotRepository localShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
    this.localShotRepository = localShotRepository;
  }

  public void dismiss(String idHighlightedShot, Boolean isAdmin, CompletedCallback completedCallback) {
    this.idHighlightedShot = idHighlightedShot;
    this.isAdmin = isAdmin;
    this.completedCallback = completedCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      if (isAdmin) {
        remoteShotRepository.dismissHighlightedShot(idHighlightedShot);
      } else {
        localShotRepository.hideHighlightedShot(idHighlightedShot);
      }
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
