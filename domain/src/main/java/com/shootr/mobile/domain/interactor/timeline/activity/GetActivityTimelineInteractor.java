package com.shootr.mobile.domain.interactor.timeline.activity;

import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.model.activity.Activity;
import com.shootr.mobile.domain.model.activity.ActivityTimeline;
import com.shootr.mobile.domain.model.activity.ActivityTimelineParameters;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetActivityTimelineInteractor implements Interactor {

  //region Dependencies
  private final InteractorHandler interactorHandler;
  private final PostExecutionThread postExecutionThread;
  private final ActivityRepository localActivityRepository;
  private Callback callback;
  private String locale;
  private Boolean isUserActivityTimeline;

  @Inject public GetActivityTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local ActivityRepository localActivityRepository) {
    this.localActivityRepository = localActivityRepository;
    this.interactorHandler = interactorHandler;
    this.postExecutionThread = postExecutionThread;
  }
  //endregion

  public void loadActivityTimeline(Boolean isUserActivityTimeline, String language,
      Callback<ActivityTimeline> callback) {
    this.isUserActivityTimeline = isUserActivityTimeline;
    this.locale = language;
    this.callback = callback;
    interactorHandler.execute(this);
  }

  @Override public void execute() throws Exception {
    loadLocalActivities();
  }

  private void loadLocalActivities() {
    ActivityTimelineParameters activityTimelineParameters = buildParameters();
    activityTimelineParameters.excludeHiddenTypes();
    List<Activity> activities = loadLocalActivities(activityTimelineParameters);
    activities = sortActivitiesByPublishDate(activities);
    ActivityTimeline timeline = buildTimeline(activities);
    notifyLoaded(timeline);
  }

  private List<Activity> loadLocalActivities(ActivityTimelineParameters timelineParameters) {
    return localActivityRepository.getActivityTimeline(timelineParameters, locale);
  }

  private ActivityTimelineParameters buildParameters() {
    return ActivityTimelineParameters.builder().isMeTimeline(isUserActivityTimeline).build();
  }

  private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
    Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
    return remoteActivities;
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

  //endregion
}
