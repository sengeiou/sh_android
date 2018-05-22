package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.SubscriptionType;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class SubscribeShotDetailInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Callback<Boolean> callback;
  private String idShot;

  @Inject public SubscribeShotDetailInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void subscribe(String idShot, Callback<Boolean> callback) {
    this.idShot = idShot;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notify(socketRepository.subscribeToShotDetail(SubscriptionType.SHOT_DETAIL, idShot));
  }

  private void notify(final Boolean response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }

}
