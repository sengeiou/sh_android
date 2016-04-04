package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SessionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderActivityTimelineInteractor implements com.shootr.mobile.domain.interactor.Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ActivityRepository remoteActivityRepository;
    private final SessionRepository sessionRepository;

    private Long currentOldestDate;
    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;
    private String language;
    private Boolean isUserActivityTimeline;

    @Inject public GetOlderActivityTimelineInteractor(InteractorHandler interactorHandler,
                                                      PostExecutionThread postExecutionThread, @Remote ActivityRepository remoteActivityRepository, SessionRepository sessionRepository) {
        this.remoteActivityRepository = remoteActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
    }

    public void loadOlderActivityTimeline(Boolean isUserActivityTimeline, Long currentOldestDate, String language, Callback<ActivityTimeline> callback,
                                          ErrorCallback errorCallback) {
        this.isUserActivityTimeline = isUserActivityTimeline;
        this.currentOldestDate = currentOldestDate;
        this.language = language;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        try {
            ActivityTimelineParameters timelineParameters = buildTimelineParameters();
            timelineParameters.excludeHiddenTypes();
            List<Activity> olderActivities = remoteActivityRepository.getActivityTimeline(timelineParameters, language);
            sortActivitiesByPublishDate(olderActivities);
            notifyTimelineFromActivities(olderActivities);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private ActivityTimelineParameters buildTimelineParameters() {
        ActivityTimelineParameters build = ActivityTimelineParameters.builder() //
          .maxDate(currentOldestDate) //
          .build();
        build.excludeHiddenTypes();
        return build;
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    //region Result
    private void notifyTimelineFromActivities(List<Activity> activities) {
        if (isUserActivityTimeline) {
            ActivityTimeline timeline = buildTimeline(retainUsersActivity(activities));
            notifyLoaded(timeline);
        } else {
            ActivityTimeline timeline = buildTimeline(activities);
            notifyLoaded(timeline);
        }
    }

    private List<Activity> retainUsersActivity(List<Activity> activities) {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<Activity> userActivities = new ArrayList<>();
        for (Activity activity : activities) {
            if (activity.getIdTargetUser() != null && activity.getIdTargetUser().equals(currentUserId)) {
                userActivities.add(activity);
            }
        }
        return userActivities;
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
