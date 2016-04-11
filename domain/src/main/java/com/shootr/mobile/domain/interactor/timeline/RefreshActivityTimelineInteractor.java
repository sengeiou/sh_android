package com.shootr.mobile.domain.interactor.timeline;

import com.shootr.mobile.domain.Activity;
import com.shootr.mobile.domain.ActivityTimeline;
import com.shootr.mobile.domain.User;
import com.shootr.mobile.domain.exception.ShootrException;
import com.shootr.mobile.domain.executor.PostExecutionThread;
import com.shootr.mobile.domain.interactor.Interactor;
import com.shootr.mobile.domain.interactor.InteractorHandler;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.domain.repository.UserRepository;
import com.shootr.mobile.domain.service.shot.ShootrTimelineService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class RefreshActivityTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShootrTimelineService shootrTimelineService;
    private final SessionRepository sessionRepository;
    private final UserRepository localUserRepository;

    private String language;
    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;
    private Boolean isUserActivityTimeline;

    @Inject public RefreshActivityTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, ShootrTimelineService shootrTimelineService,
      SessionRepository sessionRepository, @Local UserRepository localUserRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.shootrTimelineService = shootrTimelineService;
        this.sessionRepository = sessionRepository;
        this.localUserRepository = localUserRepository;
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
            ActivityTimeline activityTimeline = shootrTimelineService.refreshTimelinesForActivity(language);
            List<Activity> activities = activityTimeline.getActivities();
            List<Activity> userActivities = retainUsersActivity(activities);
            if (isUserActivityTimeline) {
                notifyCustomTimeline(activityTimeline, userActivities);
            } else {
                activities.removeAll(userActivities);
                notifyCustomTimeline(activityTimeline, activities);
            }
            User user = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
            if (user != null && user.getIdWatchingStream() != null) {
                shootrTimelineService.refreshTimelinesForStream(user.getIdWatchingStream(), false);
            }
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private void notifyCustomTimeline(ActivityTimeline activityTimeline, List<Activity> userActivities) {
        activityTimeline.setActivities(userActivities);
        notifyLoaded(activityTimeline);
    }

    private List<Activity> retainUsersActivity(List<Activity> activities) {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<Activity> userActivities = new ArrayList<>();
        for (Activity activity : activities) {
            if (isCurrentUserTargetOrAuthor(currentUserId, activity)) {
                userActivities.add(activity);
            }
        }
        return userActivities;
    }

    private boolean isCurrentUserTargetOrAuthor(String currentUserId, Activity activity) {
        return isCurrentUserTarget(currentUserId, activity) || isCurrentUserAuthor(currentUserId, activity);
    }

    private boolean isCurrentUserAuthor(String currentUserId, Activity activity) {
        return activity.getIdUser() != null && activity.getIdUser().equals(currentUserId);
    }

    private boolean isCurrentUserTarget(String currentUserId, Activity activity) {
        return activity.getIdTargetUser() != null && activity.getIdTargetUser().equals(currentUserId);
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
