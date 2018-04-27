package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetCachedNicestTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository streamRepository;

  private Callback<StreamTimeline> callback;
  private String idStream;
  private long period;

  @Inject public GetCachedNicestTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local StreamRepository streamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.streamRepository = streamRepository;
  }

  public void getTimeline(String idStream, long period, Callback<StreamTimeline> callback) {
    this.idStream = idStream;
    this.period = period;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    StreamTimeline streamTimeline =
        streamRepository.getCachedNicestTimeline(idStream, TimelineType.NICEST, period);
    notify(streamTimeline);
  }

  private void notify(final StreamTimeline response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }
}
