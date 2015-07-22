package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Stream;
import com.shootr.android.domain.EventTimelineParameters;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.exception.ShootrException;
import com.shootr.android.domain.exception.TimelineException;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.StreamRepository;
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
    private final StreamRepository localStreamRepository;
    private final UserRepository localUserRepository;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public GetEventTimelineInteractor(InteractorHandler interactorHandler,
                                              PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
                                              @Local ShotRepository localShotRepository,
                                              @Local StreamRepository localStreamRepository,
                                              @Local UserRepository localUserRepository) {
        this.sessionRepository = sessionRepository;
        this.localShotRepository = localShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localStreamRepository = localStreamRepository;
        this.localUserRepository = localUserRepository;
    }
    //endregion

    public void loadEventTimeline(Callback<Timeline> callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Exception {
        loadLocalShots();
    }

    private void loadLocalShots() {
        Stream visibleStream = getVisibleEvent();
        if (visibleStream != null) {
            List<Shot> shots = loadLocalShots(buildParameters(visibleStream));
            shots = sortShotsByPublishDate(shots);
            notifyTimelineFromShots(shots);
        } else {
            notifyError(new TimelineException("Can't load event timeline without visible event"));
        }
    }

    private List<Shot> loadLocalShots(EventTimelineParameters timelineParameters) {
        return localShotRepository.getShotsForEventTimeline(timelineParameters);
    }

    private EventTimelineParameters buildParameters(Stream stream) {
        return EventTimelineParameters.builder()
          .forEvent(stream)
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

    private Stream getVisibleEvent() {
        String visibleEventId = localUserRepository.getUserById(sessionRepository.getCurrentUserId()).getIdWatchingEvent();
        if (visibleEventId != null) {
            return localStreamRepository.getStreamById(visibleEventId);
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
