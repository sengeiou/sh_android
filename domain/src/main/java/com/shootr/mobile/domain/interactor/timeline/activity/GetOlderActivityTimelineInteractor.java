package com.shootr.mobile.domain.interactor.timeline.activity;

import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Remote;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderActivityTimelineInteractor
    implements com.shootr.mobile.domain.interactor.Interactor {

  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ActivityRepository remoteActivityRepository;

  private Long currentOldestDate;
  private Callback<ActivityTimeline> callback;
  private ErrorCallback errorCallback;
  private String language;
  private Boolean isUserActivityTimeline;

  @Inject public GetOlderActivityTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Remote ActivityRepository remoteActivityRepository) {
    this.remoteActivityRepository = remoteActivityRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }

  public void loadOlderActivityTimeline(Boolean isUserActivityTimeline, Long currentOldestDate,
      String language, Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
    this.isUserActivityTimeline = isUserActivityTimeline;
    this.currentOldestDate = currentOldestDate;
    this.language = language;
    this.callback = callback;
    this.errorCallback = errorCallback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    try {
      loadOlderTimeline();
    } catch (ShootrException error) {
      notifyError(error);
    }
  }

  private void loadOlderTimeline() {
    ActivityTimelineParameters timelineParameters = buildTimelineParameters();
    timelineParameters.excludeHiddenTypes();
    List<Activity> olderActivities =
        remoteActivityRepository.getActivityTimeline(timelineParameters, language);
    sortActivitiesByPublishDate(olderActivities);
    notifyCustomTimeline(olderActivities);
  }

  private ActivityTimelineParameters buildTimelineParameters() {
    ActivityTimelineParameters build = ActivityTimelineParameters.builder() //
        .maxDate(currentOldestDate) //
        .isMeTimeline(isUserActivityTimeline) //
        .build();
    build.excludeHiddenTypes();
    return build;
  }

  private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
    Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
    return remoteActivities;
  }

  private void notifyCustomTimeline(List<Activity> activities) {
    ActivityTimeline timeline = buildTimeline(activities);
    notifyLoaded(timeline);
  }

  private ActivityTimeline buildTimeline(List<Activity> activities) {
    ActivityTimeline timeline = new ActivityTimeline();
    timeline.setActivities(activities);
    return timeline;
  }

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
