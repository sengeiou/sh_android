package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.TimelineException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetEventTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final ShotRepository localShotRepository;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public GetEventTimelineInteractor(InteractorHandler interactorHandler,
                                              PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
                                              @Local ShotRepository localShotRepository,
                                              @Local EventRepository localEventRepository,
                                              @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.localShotRepository = localShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
    }
    //endregion

    public void loadEventTimeline(Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        loadLocalShots();
    }

    private void loadLocalShots() {
        Event visibleEvent = getVisibleEvent();
        if (visibleEvent != null) {
            List<Shot> shots = loadLocalShots(buildParameters(visibleEvent));
            shots = sortShotsByPublishDate(shots);
            notifyTimelineFromShots(shots);
        } else {
            notifyError(new TimelineException("Can't load event timeline without visible event"));
        }
    }

    private List<Shot> loadLocalShots(TimelineParameters timelineParameters) {
        return localShotRepository.getShotsForTimeline(timelineParameters);
    }

    private TimelineParameters buildParameters(Event event) {
        return TimelineParameters.builder()
          .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId())
          .forEvent(event)
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

    private Event getVisibleEvent() {
        String visibleEventId = localUserRepository.getUserById(sessionRepository.getCurrentUserId()).getIdWatchingEvent();
        if (visibleEventId != null) {
            return localEventRepository.getEventById(visibleEventId);
        }
        return null;
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
