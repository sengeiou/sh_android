package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private Long idEvent;
    private Callback callback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepository,
      @Local WatchRepository localWatchRepository, @Remote WatchRepository remoteWatchRepository,
      SessionRepository sessionRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;
    }
    //endregion

    public void selectEvent(Long idEvent, Callback callback) {
        this.idEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        stopWatchingLapsedEvent();

        Watch oldVisibleEventWatch = localWatchRepository.getCurrentVisibleWatch();
        if (oldVisibleEventWatch != null) {
            hideOldVisibleEvent(oldVisibleEventWatch);
        }
        if (oldVisibleEventWatch == null || (!oldVisibleEventWatch.getIdEvent().equals(idEvent))) {
            setNewVisibleEvent();
        }
    }

    private void stopWatchingLapsedEvent() {
        Watch currentWatching = localWatchRepository.getCurrentWatching();
        if (currentWatching != null) {
            Event eventWatching = localEventRepository.getEventById(currentWatching.getIdEvent());
            if (eventWatching != null) {
                boolean isEventLapsed = eventWatching.getEndDate().before(timeUtils.getCurrentDate());
                if (isEventLapsed) {
                    currentWatching.setWatching(false);
                    currentWatching.setNotificaticationsEnabled(false);
                    localWatchRepository.putWatch(currentWatching);
                    remoteWatchRepository.putWatch(currentWatching);
                }
            }
        }
    }

    private void setNewVisibleEvent() {
        Watch selectedEventWatch =
          localWatchRepository.getWatchForUserAndEvent(sessionRepository.getCurrentUser(), idEvent);
        if (selectedEventWatch == null) {
            selectedEventWatch = createWatch();
        }
        selectedEventWatch.setVisible(true);

        localWatchRepository.putWatch(selectedEventWatch);
        notifyLoaded(selectedEventWatch);
        remoteWatchRepository.putWatch(selectedEventWatch);
    }

    private void hideOldVisibleEvent(Watch oldVisibleEventWatch) {
        oldVisibleEventWatch.setVisible(false);

        localWatchRepository.putWatch(oldVisibleEventWatch);
        remoteWatchRepository.putWatch(oldVisibleEventWatch);
    }

    //TODO Don't like this, Interactor is probablly not the best place for creatin objects
    private Watch createWatch() {
        Watch newWatch = new Watch();
        newWatch.setIdEvent(idEvent);
        newWatch.setUser(sessionRepository.getCurrentUser());
        newWatch.setNotificaticationsEnabled(false);
        newWatch.setWatching(false);
        newWatch.setVisible(false);
        newWatch.setUserStatus("Watching"); //TODO localize
        return newWatch;
    }

    private void notifyLoaded(final Watch watch) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(watch);
            }
        });
    }

    public interface Callback {

        void onLoaded(Watch watch);

    }
}