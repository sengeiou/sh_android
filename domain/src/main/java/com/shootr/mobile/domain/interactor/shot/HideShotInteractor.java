package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import java.util.Date;
import javax.inject.Inject;

public class HideShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalShotRepository remoteShotRepository;

  private String idShot;
  private CompletedCallback completedCallback;

  @Inject HideShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void hideShot(String idShot, CompletedCallback completedCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      sendHideShotToServer();
    } catch (ServerCommunicationException e) {
            /* Ignore error and notify callback */
    }
    notifyCompleted();
  }

  private void sendHideShotToServer() {
    Date now = new Date();
    remoteShotRepository.hideShot(idShot, now.getTime(), StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
