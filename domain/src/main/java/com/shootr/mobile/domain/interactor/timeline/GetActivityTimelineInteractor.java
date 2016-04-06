package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.ActivityTimelineParameters;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.ActivityRepository;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetActivityTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ActivityRepository localActivityRepository;
    private final SessionRepository sessionRepository;
    private Callback callback;
    private String locale;
    private Boolean isUserActivityTimeline;

    @Inject
    public GetActivityTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
                                         @Local ActivityRepository localActivityRepository, SessionRepository sessionRepository) {
        this.localActivityRepository = localActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.sessionRepository = sessionRepository;
    }
    //endregion

    public void loadActivityTimeline(Boolean isUserActivityTimeline, String language, Callback<ActivityTimeline> callback) {
        this.isUserActivityTimeline = isUserActivityTimeline;
        this.locale = language;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override
    public void execute() throws Exception {
        loadLocalActivities();
    }

    private void loadLocalActivities() {
        ActivityTimelineParameters activityTimelineParameters = buildParameters();
        activityTimelineParameters.excludeHiddenTypes();
        List<Activity> activities = loadLocalActivities(activityTimelineParameters);
        activities = sortActivitiesByPublishDate(activities);
        notifyTimelineFromActivities(activities);
    }

    private List<Activity> loadLocalActivities(ActivityTimelineParameters timelineParameters) {
        return localActivityRepository.getActivityTimeline(timelineParameters, locale);
    }

    private ActivityTimelineParameters buildParameters() {
        return ActivityTimelineParameters.builder().build();
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    //region Result
    private void notifyTimelineFromActivities(List<Activity> activities) {
        List<Activity> userActivities = retainUsersActivity(activities);
        if (isUserActivityTimeline) {
            ActivityTimeline timeline = buildTimeline(userActivities);
            notifyLoaded(timeline);
        } else {
            activities.removeAll(userActivities);
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
            @Override
            public void run() {
                callback.onLoaded(timeline);
            }
        });
    }

    //endregion
}
