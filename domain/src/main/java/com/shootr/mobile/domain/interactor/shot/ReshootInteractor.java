package com.shootr.mobile.domain.interactor.shot;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.shot.ExternalShotRepository;
import com.shootr.mobile.domain.repository.shot.InternalShotRepository;
import javax.inject.Inject;

public class ReshootInteractor implements Interactor {

  private final ExternalShotRepository remoteShotRepository;
  private final InternalShotRepository internalShotRepository;
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private String idShot;
  private CompletedCallback completedCallback;
  private ErrorCallback errorCallback;

  @Inject public ReshootInteractor(ExternalShotRepository remoteShotRepository,
      InternalShotRepository internalShotRepository, InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread) {
    this.remoteShotRepository = remoteShotRepository;
    this.internalShotRepository = internalShotRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void reshoot(String idShot, CompletedCallback callback, ErrorCallback errorCallback) {
    this.idShot = idShot;
    this.completedCallback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      internalShotRepository.reshoot(idShot);
      remoteShotRepository.reshoot(idShot);
      notifyCompleted();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  protected void notifyCompleted() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        completedCallback.onCompleted();
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
}
