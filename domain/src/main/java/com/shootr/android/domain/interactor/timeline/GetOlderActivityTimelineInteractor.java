package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.ActivityTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GetOlderActivityTimelineInteractor implements Interactor {

    private final SessionRepository sessionRepository;
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final ShotRepository remoteShotRepository;
    private final UserRepository localUserRepository;

    private Long currentOldestDate;
    private Callback<Timeline> callback;
    private ErrorCallback errorCallback;

    @Inject public GetOlderActivityTimelineInteractor(InteractorHandler interactorHandler,
                                                      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
                                                      @Remote ShotRepository remoteShotRepository, @Local EventRepository localEventRepository, @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localUserRepository = localUserRepository;
    }

    public void loadOlderActivityTimeline(Long currentOldestDate, Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.currentOldestDate = currentOldestDate;
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        try {
            ActivityTimelineParameters timelineParameters = buildTimelineParameters();
            List<Shot> olderShots = remoteShotRepository.getShotsForActivityTimeline(timelineParameters);
            sortShotsByPublishDate(olderShots);
            notifyTimelineFromShots(olderShots);
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private ActivityTimelineParameters buildTimelineParameters() {
        return ActivityTimelineParameters.builder() //
                .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()) //
                .maxDate(currentOldestDate) //
                .build();
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    private List<String> getPeopleIds() {
        List<String> ids = new ArrayList<>();
        for (User user : localUserRepository.getPeople()) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    //region Result
    private void notifyTimelineFromShots(List<Shot> shots) {
        Timeline timeline = buildTimeline(shots);
        notifyLoaded(timeline);
    }

    private Timeline buildTimeline(List<Shot> shots) {
        Timeline timeline = new Timeline();
        timeline.setShots(shots);
        return timeline;
    }

    private void notifyLoaded(final Timeline timeline) {
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