package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Activity;
import com.shootr.android.domain.ActivityTimeline;
import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.ActivityRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetActivityTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final ActivityRepository localActivityRepository;
    private final UserRepository localUserRepository;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public GetActivityTimelineInteractor(InteractorHandler interactorHandler,
                                                 PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
                                                 @Local ActivityRepository localActivityRepository,
                                                 @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.localActivityRepository = localActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
    }
    //endregion

    public void loadActivityTimeline(Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
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
          .currentUser(sessionRepository.getCurrentUserId())
          .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId())
          .build();
    }

    private List<Activity> sortActivitiesByPublishDate(List<Activity> remoteActivities) {
        Collections.sort(remoteActivities, new Activity.NewerAboveComparator());
        return remoteActivities;
    }

    private List<String> getPeopleIds() {
        List<String> ids = new ArrayList<>();
        for (User user : localUserRepository.getPeople()) {
            ids.add(user.getIdUser());
        }
        return ids;
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

    private void notifyError(final ShootrException error) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                errorCallback.onError(error);
            }
        });
    }

    //endregion
}
