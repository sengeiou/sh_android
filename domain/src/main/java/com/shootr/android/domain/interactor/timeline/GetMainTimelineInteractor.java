package com.shootr.android.domain.interactor.timeline;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Shot;
import com.shootr.android.domain.Timeline;
import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.ShotRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class GetMainTimelineInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final WatchRepository remoteWatchRepository;
    private final ShotRepository localShotRepository;
    private final ShotRepository remoteShotRepository;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private Callback callback;

    @Inject public GetMainTimelineInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local ShotRepository localShotRepository, @Remote ShotRepository remoteShotRepository, @Remote WatchRepository remoteWatchRepository,
      @Local EventRepository localEventRepository, @Local UserRepository localUserRepository) {
        this.localShotRepository = localShotRepository;
        this.remoteShotRepository = remoteShotRepository;
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.remoteWatchRepository = remoteWatchRepository;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
    }
    //endregion

    public void loadMainTimeline(Callback callback) {
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Event visibleEvent = getVisibleEvent();
        if (visibleEvent != null) {
            loadShotsWithEvent(visibleEvent);
        } else {
            loadShotsWithoutEvent();
        }
    }

    private void loadShotsWithoutEvent() {
        List<Shot> localShots = localShotRepository.getShotsWithoutEventFromUsers(getPeopleIds());
        notifyTimelineFromShots(localShots);
        List<Shot> remoteShots = remoteShotRepository.getShotsWithoutEventFromUsers(getPeopleIds());
        notifyTimelineFromShots(remoteShots);
    }

    private void loadShotsWithEvent(Event visibleEvent) {
        List<Shot> localShotsWithAuthor =
          localShotRepository.getShotsForEventAndUsersWithAuthor(visibleEvent.getId(), visibleEvent.getAuthorId(), getPeopleIds());
        notifyTimelineFromShots(localShotsWithAuthor);

        List<Shot> remoteShotsWithAuthor =
          remoteShotRepository.getShotsForEventAndUsersWithAuthor(visibleEvent.getId(), visibleEvent.getAuthorId(), getPeopleIds());
        notifyTimelineFromShots(remoteShotsWithAuthor);
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
        Watch currentVisibleWatch = remoteWatchRepository.getCurrentVisibleWatch();
        if (currentVisibleWatch != null) {
            return localEventRepository.getEventById(currentVisibleWatch.getIdEvent());
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
