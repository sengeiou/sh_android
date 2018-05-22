package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;


public class UnsubscribeShotDetailInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Interactor.Callback<Boolean> callback;
  private String idShot;

  @Inject public UnsubscribeShotDetailInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void unsubscribe(String idShot, Interactor.Callback<Boolean> callback) {
    this.idShot = idShot;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    socketRepository.unsubscribeShotDetail(idShot);
    notify(true);
  }

  private void notify(final boolean result) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(result);
      }
    });
  }
}
