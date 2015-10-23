package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetActivityTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ActivityRepository localActivityRepository;
    private Callback callback;

    @Inject public GetActivityTimelineInteractor(InteractorHandler interactorHandler,
                                                 PostExecutionThread postExecutionThread,
                                                 @Local ActivityRepository localActivityRepository) {
        this.localActivityRepository = localActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
    }
    //endregion

    public void loadActivityTimeline(Callback<ActivityTimeline> callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalActivities();
    }

    private void loadLocalActivities() {
        List<Activity> activities = loadLocalActivities(buildParameters());
        activities = sortActivitiesByPublishDate(activities);
        notifyTimelineFromActivities(activities);
    }

    private List<Activity> loadLocalActivities(ActivityTimelineParameters timelineParameters) {
        return localActivityRepository.getActivityTimeline(timelineParameters);
    }

    private ActivityTimelineParameters buildParameters() {
        return ActivityTimelineParameters.builder()
          .build();
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    //region Result
    private void notifyTimelineFromActivities(List<Activity> activities) {
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

    //endregion
}
