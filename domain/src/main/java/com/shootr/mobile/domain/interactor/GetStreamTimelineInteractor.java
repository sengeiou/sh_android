package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class GetStreamTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final ExternalTimelineRepository timelineRepository;
  private final PostExecutionThread postExecutionThread;

  private Interactor.Callback<StreamTimeline> callback;
  private String idStream;
  private String timelineType;
  private long timestamp;
  private boolean isPaginating;

  @Inject public GetStreamTimelineInteractor(InteractorHandler interactorHandler,
      ExternalTimelineRepository timelineRepository, PostExecutionThread postExecutionThread) {
    this.interactorHandler = interactorHandler;
    this.timelineRepository = timelineRepository;
    this.postExecutionThread = postExecutionThread;
  }

  public void getTimeline(String idStream, String timelineType, long timestamp,
      boolean isPaginating, Interactor.Callback<StreamTimeline> callback) {
    this.callback = callback;
    this.idStream = idStream;
    this.timelineType = timelineType;
    this.timestamp = timestamp;
    this.isPaginating = isPaginating;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      notify(
          isPaginating ? timelineRepository.getPaginatedTimeline(idStream, timelineType, timestamp)
              : timelineRepository.getTimeline(idStream, timelineType, timestamp));
    } catch (ShootrException error) {
      //TODO notifcar este error
    }
  }

  private void notify(final StreamTimeline streamTimeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(streamTimeline);
      }
    });
  }
}
