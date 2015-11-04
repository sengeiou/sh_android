package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.ActivityTimeline;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderActivityTimelineInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler;
    private final com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread;
    private final com.shootr.mobile.domain.repository.ActivityRepository remoteActivityRepository;

    private Long currentOldestDate;
    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;

    @Inject public GetOlderActivityTimelineInteractor(
      com.shootr.mobile.domain.interactor.InteractorHandler interactorHandler,
                                                      com.shootr.mobile.domain.executor.PostExecutionThread postExecutionThread,
                                                      @com.shootr.mobile.domain.repository.Remote
                                                      com.shootr.mobile.domain.repository.ActivityRepository remoteActivityRepository) {
        this.remoteActivityRepository = remoteActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }

    public void loadOlderActivityTimeline(Long currentOldestDate, Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            com.shootr.mobile.domain.ActivityTimelineParameters timelineParameters = buildTimelineParameters();
            List<com.shootr.mobile.domain.Activity> olderActivities = remoteActivityRepository.getActivityTimeline(timelineParameters);
            sortActivitiesByPublishDate(olderActivities);
            notifyTimelineFromActivities(olderActivities);
        } catch (com.shootr.mobile.domain.exception.ShootrException error) {
            notifyError(error);
        }
    }

    private com.shootr.mobile.domain.ActivityTimelineParameters buildTimelineParameters() {
        com.shootr.mobile.domain.ActivityTimelineParameters
          build = com.shootr.mobile.domain.ActivityTimelineParameters.builder() //
          .maxDate(currentOldestDate) //
          .build();
        build.excludeHiddenTypes();
        return build;
    }

    private List<com.shootr.mobile.domain.Activity> sortActivitiesByPublishDate(List<com.shootr.mobile.domain.Activity> remoteActivities) {
        Collections.sort(remoteActivities, new com.shootr.mobile.domain.Activity.NewerAboveComparator());
        return remoteActivities;
    }

    //region Result
    private void notifyTimelineFromActivities(List<com.shootr.mobile.domain.Activity> activities) {
        ActivityTimeline timeline = buildTimeline(activities);
        notifyLoaded(timeline);
    }

    private ActivityTimeline buildTimeline(List<com.shootr.mobile.domain.Activity> activities) {
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

    private void notifyError(final com.shootr.mobile.domain.exception.ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }
    //endregion
}
