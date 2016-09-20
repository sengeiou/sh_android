package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.ShotRepository;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import javax.inject.Inject;

public class UnmarkNiceShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalNiceShotRepository localNiceShotRepository;
  private final NiceShotRepository remoteNiceShotRepository;
  private final ShotRepository localShotRepository;
  private final ShotRepository remoteShotRepository;

  private String idShot;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  @Inject public UnmarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalNiceShotRepository localNiceShotRepository,
      @Remote NiceShotRepository remoteNiceShotRepository,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localNiceShotRepository = localNiceShotRepository;
    this.remoteNiceShotRepository = remoteNiceShotRepository;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void unmarkNiceShot(String idShot, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {

    sendUndoNiceToRemote();
    notifyCompleted();
  }

  private void unmarkNiceInLocal() throws NiceNotMarkedException {
    localNiceShotRepository.unmark(idShot);
    Shot shot = getShotFromLocalIfExists();
    shot.setNiceCount(shot.getNiceCount() - 1);
    localShotRepository.putShot(shot);
  }

  private Shot getShotFromLocalIfExists() {
    Shot shot = localShotRepository.getShot(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    if (shot == null) {
      shot = remoteShotRepository.getShot(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    }
    return shot;
  }

  protected void sendUndoNiceToRemote() {
    try {
      remoteNiceShotRepository.unmark(idShot);
      unmarkNiceInLocal();
    } catch (ShootrException | NiceNotMarkedException e) {
      notifyError(new ShootrException() {
      });
    }
  }

  protected void notifyCompleted() {
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
