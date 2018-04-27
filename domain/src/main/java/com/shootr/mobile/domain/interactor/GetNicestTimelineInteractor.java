package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class GetNicestTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Callback<Boolean> callback;
  private String idStream;
  private String filterType;
  private long timestamp;
  private long duration;

  @Inject public GetNicestTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void getTimeline(String idStream, long timestamp, long period,
      Callback<Boolean> callback) {
    this.idStream = idStream;
    this.timestamp = timestamp;
    this.callback = callback;
    this.filterType = TimelineType.NICEST;
    this.duration = period;

    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Pagination pagination = new Pagination();
    pagination.setSinceTimestamp(timestamp);

    notify(socketRepository.getNicestTimeline(idStream, filterType, pagination, duration));
  }

  private void notify(final Boolean response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }
}
