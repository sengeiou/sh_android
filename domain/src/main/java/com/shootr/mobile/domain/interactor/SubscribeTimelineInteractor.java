package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class SubscribeTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Interactor.Callback<Boolean> callback;
  private String idStream;
  private String filterType;
  private long period;

  @Inject public SubscribeTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void subscribe(String idStream, String filterType, Interactor.Callback<Boolean> callback) {
    this.idStream = idStream;
    this.filterType = filterType;
    this.callback = callback;
    this.period = 0;
    interactorHandler.execute(this);
  }

  public void subscribe(String idStream, String filterType, long period, Interactor.Callback<Boolean> callback) {
    this.idStream = idStream;
    this.filterType = filterType;
    this.callback = callback;
    this.period = period;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notify(socketRepository.subscribeToTimeline("TIMELINE", idStream, filterType, period));
  }

  private void notify(final Boolean response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }

}
