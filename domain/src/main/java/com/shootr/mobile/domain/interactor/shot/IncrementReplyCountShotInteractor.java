package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.Shot;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import javax.inject.Inject;

public class IncrementReplyCountShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShotRepository localShotRepository;
  private final ShotRepository remoteShotRepository;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;
  private String idShot;

  @Inject public IncrementReplyCountShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ShotRepository localShotRepository,
      @Remote ShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void incrementReplyCount(String idShot, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Shot shot = getShot();
      incrementReplyCount(shot);
      notifyCompleted();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void incrementReplyCount(Shot shot) {
    shot.setReplyCount(shot.getReplyCount() + 1);
    localShotRepository.putShot(shot);
  }

  private Shot getShot() {
    Shot shot = localShotRepository.getShot(idShot);
    if (shot == null) {
      shot = remoteShotRepository.getShot(idShot);
    }
    return shot;
  }

  private void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }

  protected void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
