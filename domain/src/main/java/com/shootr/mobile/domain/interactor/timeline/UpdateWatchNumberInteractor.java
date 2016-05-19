package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.WatchUpdateRequest;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import javax.inject.Inject;

public class UpdateWatchNumberInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final BusPublisher busPublisher;

  private Interactor.CompletedCallback callback;

  @Inject public UpdateWatchNumberInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, BusPublisher busPublisher) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.busPublisher = busPublisher;
  }

  public void updateWatchNumber(CompletedCallback callback) {
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    busPublisher.post(new WatchUpdateRequest.Event());
    notifyLoaded();
  }

  protected void notifyLoaded() {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onCompleted();
      }
    });
  }
}
