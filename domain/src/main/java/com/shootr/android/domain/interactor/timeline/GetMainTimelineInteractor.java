package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
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
import com.shootr.android.domain.repository.SynchronizationRepository;
import com.shootr.android.domain.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;

public class GetMainTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SynchronizationRepository synchronizationRepository;
    private Callback callback;
    private ErrorCallback errorCallback;

    @Inject public GetMainTimelineInteractor(InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, SessionRepository sessionRepository,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository,
      @Remote UserRepository remoteUserRepository, @Local EventRepository localEventRepository,
      @Local UserRepository localUserRepository, SynchronizationRepository synchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteUserRepository = remoteUserRepository;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.synchronizationRepository = synchronizationRepository;
    }
    //endregion

    public void loadMainTimeline(Callback callback, ErrorCallback errorCallback) {
        this.callback = callback;
        this.errorCallback = errorCallback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        loadLocalShots();
        loadRemoteShots();
    }

    private void loadLocalShots() {
        Event localVisibleEvent = getVisibleEventFromRepository(localUserRepository);
        if (localVisibleEvent == null) {
            loadLocalShotsWithoutEvent(buildParametersWithoutEvent());
        } else {
            loadLocalShotsWithEvent(buildParametersWithEvent(localVisibleEvent));
        }
    }

    private void loadRemoteShots() {
        try {
            Event remoteVisibleEvent = getVisibleEventFromRepository(remoteUserRepository);
            if (remoteVisibleEvent == null) {
                loadRemoteShotsWithoutEvent(buildParametersWithoutEvent());
            } else {
                loadRemoteShotsWithEvent(buildParametersWithEvent(remoteVisibleEvent));
            }
        } catch (ShootrException error) {
            notifyError(error);
        }
    }

    private void loadLocalShotsWithoutEvent(TimelineParameters timelineParameters) {
        List<Shot> localShots = localShotRepository.getShotsForTimeline(timelineParameters);
        localShots = sortShotsByPublishDate(localShots);
        if (!localShots.isEmpty()) {
            notifyTimelineFromShots(localShots);
        }
    }

    private void loadRemoteShotsWithoutEvent(TimelineParameters timelineParameters) {
        List<Shot> remoteShots = remoteShotRepository.getShotsForTimeline(timelineParameters);
        remoteShots = sortShotsByPublishDate(remoteShots);
        notifyTimelineFromShots(remoteShots);
        updateLastRefreshDate(remoteShots);
    }

    private void loadLocalShotsWithEvent(TimelineParameters timelineParameters) {
        List<Shot> localShotsWithAuthor = localShotRepository.getShotsForTimeline(timelineParameters);
        localShotsWithAuthor = sortShotsByPublishDate(localShotsWithAuthor);
        if (!localShotsWithAuthor.isEmpty()) {
            notifyTimelineFromShots(localShotsWithAuthor);
        }
    }

    private void loadRemoteShotsWithEvent(TimelineParameters timelineParameters) {
        List<Shot> remoteShotsWithAuthor = remoteShotRepository.getShotsForTimeline(timelineParameters);
        remoteShotsWithAuthor = sortShotsByPublishDate(remoteShotsWithAuthor);
        notifyTimelineFromShots(remoteShotsWithAuthor);
        updateLastRefreshDate(remoteShotsWithAuthor);
    }

    private TimelineParameters buildParametersWithoutEvent() {
        return TimelineParameters.builder().forUsers(getPeopleIds(), sessionRepository.getCurrentUserId()).build();
    }

    private TimelineParameters buildParametersWithEvent(Event event) {
        return TimelineParameters.builder()
          .forUsers(getPeopleIds(), sessionRepository.getCurrentUserId())
          .forEvent(event)
          .build();
    }

    private void updateLastRefreshDate(List<Shot> remoteShots) {
        if (remoteShots.size() > 0) {
            synchronizationRepository.putTimelineLastRefresh(remoteShots.get(0).getPublishDate().getTime());
        }
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.NewerAboveComparator());
        return remoteShots;
    }

    private List<Long> getPeopleIds() {
        List<Long> ids = new ArrayList<>();
        for (User user : localUserRepository.getPeople()) {
            ids.add(user.getIdUser());
        }
        return ids;
    }

    private Event getVisibleEventFromRepository(UserRepository userRepository) {
        Long visibleEventId = userRepository.getUserById(sessionRepository.getCurrentUserId()).getVisibleEventId();
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

    public interface Callback {

        void onLoaded(Timeline timeline);
    }
    //endregion
}
