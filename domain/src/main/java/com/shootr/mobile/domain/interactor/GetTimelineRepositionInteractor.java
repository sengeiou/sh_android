package com.shootr.mobile.domain.interactor;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.model.TimelineReposition;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import javax.inject.Inject;

public class GetTimelineRepositionInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final StreamRepository streamRepository;

  private Callback<TimelineReposition> callback;
  private String idStream;
  private String filterType;

  @Inject public GetTimelineRepositionInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread,
      @Local StreamRepository streamRepository) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.streamRepository = streamRepository;
  }

  public void getTimeline(String idStream, String filterType,
      Callback<TimelineReposition> callback) {
    this.idStream = idStream;
    this.filterType = filterType;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    notify(streamRepository.getTimelineReposition(idStream, filterType));
  }

  private void notify(final TimelineReposition response) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(response);
      }
    });
  }

}
