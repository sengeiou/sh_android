package com.shootr.mobile.domain.interactor.timeline.activity;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import javax.inject.Inject;

public class RefreshActivityTimelineInteractor implements Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ShootrTimelineService shootrTimelineService;

  private String language;
  private Callback<ActivityTimeline> callback;
  private ErrorCallback errorCallback;
  private Boolean isUserActivityTimeline;

  @Inject public RefreshActivityTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService) {
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
    this.shootrTimelineService = shootrTimelineService;
  }

  public void refreshActivityTimeline(Boolean isUserActivityTimeline, String language,
      Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
    this.isUserActivityTimeline = isUserActivityTimeline;
    this.language = language;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    executeSynchronized();
  }

  private synchronized void executeSynchronized() {
    try {
      ActivityTimeline activityTimeline =
          shootrTimelineService.refreshTimelinesForActivity(language, isUserActivityTimeline);
      notifyLoaded(activityTimeline);
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  //region Result
  private void notifyLoaded(final ActivityTimeline timeline) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        callback.onLoaded(timeline);
      }
    });
  }

  private void notifyError(final ShootrException error) {
    postExecutionThread.post(new Runnable() {
      @Override public void run() {
        errorCallback.onError(error);
      }
    });
  }
  //endregion
}
