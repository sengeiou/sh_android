package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.Bootstrapping;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import javax.inject.Inject;

public class GetSocketInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalStreamRepository timelineRepository;
  private final SessionRepository sessionRepository;
  private final PostExecutionThread postExecutionThread;
  private ErrorCallback errorCallback;

  private Callback<Bootstrapping> callback;

  @Inject public GetSocketInteractor(InteractorHandler interactorHandler,
      ExternalStreamRepository timelineRepository, SessionRepository sessionRepository,
      PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.sessionRepository = sessionRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void getSocket(Callback<Bootstrapping> callback, ErrorCallback errorCallback) {
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      Bootstrapping bootstrapping = timelineRepository.getSocket();
      if (bootstrapping != null) {
        sessionRepository.setBootstrapping(bootstrapping);
        notifyLoaded(bootstrapping);
      }
      notifyLoaded(null);

    } catch (ShootrException exception) {
      notifyError(exception);
    }
  }

  private void notifyLoaded(final Bootstrapping result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
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
