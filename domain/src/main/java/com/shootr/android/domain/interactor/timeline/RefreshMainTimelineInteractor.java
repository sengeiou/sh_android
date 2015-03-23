package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.TimelineParameters;
import com.shootr.android.domain.User;
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

public class RefreshMainTimelineInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final SessionRepository sessionRepository;
    private final ShotRepository remoteShotRepository;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final SynchronizationRepository synchronizationRepository;
    private Callback callback;

    @Inject public RefreshMainTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      SessionRepository sessionRepository, @Remote ShotRepository remoteShotRepository, @Local EventRepository localEventRepository,
      @Local UserRepository localUserRepository, SynchronizationRepository synchronizationRepository) {
        this.sessionRepository = sessionRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.synchronizationRepository = synchronizationRepository;
    }

    public void refreshMainTimeline(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        executeSynchronized();
    }

    private synchronized void executeSynchronized() {
        TimelineParameters timelineParameters = buildTimelineParameters();

        List<Shot> remoteShots = remoteShotRepository.getShotsForTimeline(timelineParameters);
        remoteShots = sortShotsByPublishDate(remoteShots);
        notifyTimelineFromShots(remoteShots);

        updateLastRefreshDate(remoteShots);
    }

    private List<Shot> sortShotsByPublishDate(List<Shot> remoteShots) {
        Collections.sort(remoteShots, new Shot.PublishDateComparator());
        return remoteShots;
    }

    private void updateLastRefreshDate(List<Shot> remoteShots) {
        if (remoteShots.size() > 0) {
            synchronizationRepository.putTimelineLastRefresh(remoteShots.get(0)
              .getPublishDate()
              .getTime());
        }
    }

    private TimelineParameters buildTimelineParameters() {
        TimelineParameters.Builder timelineParametersBuilder = TimelineParameters.builder().forUsers(getPeopleIds(), sessionRepository.getCurrentUserId());
        Event visibleEvent = getVisibleEvent();
        if (visibleEvent != null) {
            timelineParametersBuilder.forEvent(visibleEvent);
        }
        Long since = synchronizationRepository.getTimelineLastRefresh();
        timelineParametersBuilder.since(since);
        return timelineParametersBuilder.build();
    }

    private List<Long> getPeopleIds() {
        List<Long> ids = new ArrayList<>();
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

    private Event getVisibleEvent() {
        Long visibleEventId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (visibleEventId != null) {
            return localEventRepository.getEventById(visibleEventId);
        }
        return null;
    }

    private void notifyLoaded(final Timeline timeline) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(timeline);
            }
        });
    }

    public interface Callback {

        void onLoaded(Timeline timeline);
    }
    //endregion
}
