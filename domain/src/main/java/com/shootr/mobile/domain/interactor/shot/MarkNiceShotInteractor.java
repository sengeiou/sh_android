package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.NiceAlreadyMarkedException;
import com.shootr.mobile.domain.exception.NiceNotMarkedException;
import com.shootr.mobile.domain.exception.ServerCommunicationException;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.exception.ShotNotFoundException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.shot.Shot;
import com.shootr.mobile.domain.model.shot.ShotType;
import com.shootr.mobile.domain.model.stream.StreamMode;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.nice.InternalNiceShotRepository;
import com.shootr.mobile.domain.repository.nice.NiceShotRepository;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import javax.inject.Inject;

public class MarkNiceShotInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final InternalNiceShotRepository localNiceShotRepository;
  private final NiceShotRepository remoteNiceShotRepository;
  private final InternalShotRepository localShotRepository;
  private final ExternalShotRepository remoteShotRepository;

  private String idShot;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  @Inject public MarkNiceShotInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, InternalNiceShotRepository localNiceShotRepository,
      @Remote NiceShotRepository remoteNiceShotRepository,
      InternalShotRepository localShotRepository, ExternalShotRepository remoteShotRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.localNiceShotRepository = localNiceShotRepository;
    this.remoteNiceShotRepository = remoteNiceShotRepository;
    this.localShotRepository = localShotRepository;
    this.remoteShotRepository = remoteShotRepository;
  }

  public void markNiceShot(String idShot, CompletedCallback completedCallback,
      ErrorCallback errorCallback) {
    this.idShot = idShot;
    this.completedCallback = completedCallback;
    this.errorCallback = errorCallback;
    this.interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    sendNiceToServer();
    notifyCompleted();
  }

  private void sendNiceToServer() throws NiceNotMarkedException {
    try {
      remoteNiceShotRepository.mark(idShot);
      markNiceInLocal();
    } catch (ServerCommunicationException | NiceAlreadyMarkedException error) {
      notifyError(new ShootrException() {
      });
    }
  }

  private void markNiceInLocal() throws NiceAlreadyMarkedException {
    try {
      localNiceShotRepository.mark(idShot);
      Shot shot = getShotFromLocalIfExists();
      shot.setNiceCount(shot.getNiceCount() + 1);
      localShotRepository.putShot(shot);
    } catch (ShotNotFoundException | ServerCommunicationException error) {
            /* swallow */
    }
  }

  private Shot getShotFromLocalIfExists() {
    Shot shot = localShotRepository.getShot(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
    if (shot == null) {
      shot = remoteShotRepository.getShot(idShot, StreamMode.TYPES_STREAM, ShotType.TYPES_SHOWN);
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
