package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.ShotEvent;
import com.shootr.mobile.domain.model.shot.ShotEventType;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.ShotEventRepository;
import javax.inject.Inject;

public class ViewHighlightedShotEventInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotEventRepository localShotEventRepository;

  private CompletedCallback completedCallback;
  private String idShot;

  @Inject public ViewHighlightedShotEventInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShotEventRepository shotEventRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotEventRepository = shotEventRepository;
  }

  public void countViewEvent(String idShot, CompletedCallback completedCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    countViewEvent();
    notifyCompleted();
  }

  private void countViewEvent() {
    ShotEvent shotEvent = new ShotEvent();
    shotEvent.setType(ShotEventType.SHOT_VIEW);
    shotEvent.setIdShot(idShot);
    localShotEventRepository.viewHighlightedShot(shotEvent);
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }
}
