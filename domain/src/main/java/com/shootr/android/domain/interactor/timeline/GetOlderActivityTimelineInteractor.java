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
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetOlderActivityTimelineInteractor implements Interactor {

    private final SessionRepository sessionRepository;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ActivityRepository remoteActivityRepository;
    private final UserRepository localUserRepository;

    private Long currentOldestDate;
    private Callback<ActivityTimeline> callback;
    private ErrorCallback errorCallback;

    @Inject public GetOlderActivityTimelineInteractor(InteractorHandler interactorHandler,
                                                      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
                                                      @Remote ActivityRepository remoteActivityRepository, @Local EventRepository localEventRepository, @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteActivityRepository = remoteActivityRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
    }

    public void loadOlderActivityTimeline(Long currentOldestDate, Callback<ActivityTimeline> callback, ErrorCallback errorCallback) {
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        try {
            ActivityTimelineParameters timelineParameters = buildTimelineParameters();
            List<Activity> olderActivities = remoteActivityRepository.getActivityTimeline(timelineParameters);
            sortActivitiesByPublishDate(olderActivities);
            notifyTimelineFromActivities(olderActivities);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private ActivityTimelineParameters buildTimelineParameters() {
        return ActivityTimelineParameters.builder() //
                .currentUser(sessionRepository.getCurrentUserId()) //
                .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()) //
                .maxDate(currentOldestDate) //
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
