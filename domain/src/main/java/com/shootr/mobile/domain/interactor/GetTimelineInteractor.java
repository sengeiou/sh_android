package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import javax.inject.Inject;

public class GetTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final SocketRepository socketRepository;

  private Callback<Boolean> callback;
  private String idStream;
  private String filterType;
  private long timestamp;
  private boolean isPaginating;

  @Inject public GetTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Remote SocketRepository socketRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.socketRepository = socketRepository;
  }

  public void getTimeline(String idStream, String filterType, long timestamp, boolean isPaginating,
      Callback<Boolean> callback) {
    this.idStream = idStream;
    this.filterType = filterType;
    this.timestamp = timestamp;
    this.callback = callback;
    this.isPaginating = isPaginating;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    Pagination pagination = new Pagination();
    if (isPaginating) {
      pagination.setMaxTimestamp(timestamp);
    } else {
      pagination.setSinceTimestamp(timestamp);
    }
    notify(socketRepository.getTimeline(idStream, filterType, pagination));
  }

  private void notify(final Boolean response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }

}
