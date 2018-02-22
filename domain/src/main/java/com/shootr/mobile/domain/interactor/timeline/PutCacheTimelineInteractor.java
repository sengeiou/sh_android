package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.StreamTimeline;
import com.shootr.mobile.domain.repository.timeline.ExternalTimelineRepository;
import javax.inject.Inject;

public class PutCacheTimelineInteractor implements Interactor {

  //region Dependencies
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ExternalTimelineRepository timelineRepository;
  private String idStream;
  private String idFilter;
  private Callback callback;
  private StreamTimeline timeline;

  @Inject public PutCacheTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ExternalTimelineRepository timelineRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.timelineRepository = timelineRepository;
  }
  //endregion

  public void putTimeline(String idStream, String idFilter, StreamTimeline timeline) {
    this.idStream = idStream;
    this.idFilter = idFilter;
    this.timeline = timeline;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    saveTimeline();
  }

  private void saveTimeline() {
    timelineRepository.putTimeline(timeline, idStream, idFilter);
  }
}
