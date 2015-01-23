package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.LocalRepository;
import com.shootr.android.domain.repository.RemoteRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import com.shootr.android.domain.utils.TimeUtils;
import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final EventRepository eventRepository;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private Long idEvent;
    private ErrorCallback errorCallback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler, EventRepository eventRepository,
      @LocalRepository WatchRepository localWatchRepository, @RemoteRepository WatchRepository remoteWatchRepository,
      SessionRepository sessionRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.eventRepository = eventRepository;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;

        this.errorCallback = new ErrorCallback() {
            @Override public void onError(Throwable error) {
                interactorHandler.sendError(error);
            }
        };
    }
    //endregion

    public void selectEvent(Long idEvent) {
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        stopWatchingLapsedEvent();
        hideOldVisibleEvent();
        setNewVisibleEvent();
    }

    private void stopWatchingLapsedEvent() {
        Watch currentWatching = localWatchRepository.getCurrentWatching();
        if (currentWatching != null) {
            Event eventWatching = eventRepository.getEventById(currentWatching.getIdEvent());
            boolean isEventLapsed = eventWatching.getEndDate().before(timeUtils.getCurrentDate());
            if (isEventLapsed) {
                currentWatching.setWatching(false);
                currentWatching.setNotificaticationsEnabled(false);
                localWatchRepository.putWatch(currentWatching);
                remoteWatchRepository.putWatch(currentWatching);
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
        remoteWatchRepository.putWatch(selectedEventWatch);

        interactorHandler.sendUiMessage(selectedEventWatch);
    }

    private void hideOldVisibleEvent() {
        Event oldVisibleEvent = eventRepository.getVisibleEvent();
        if (oldVisibleEvent == null) {
            return;
        }
        Watch oldVisibleEventWatch =
          localWatchRepository.getWatchForUserAndEvent(sessionRepository.getCurrentUser(), oldVisibleEvent.getId(),
            errorCallback);
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
}
