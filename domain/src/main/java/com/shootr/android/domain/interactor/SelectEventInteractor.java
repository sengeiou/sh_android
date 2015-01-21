package com.shootr.android.domain.interactor;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventRepository eventRepository;
    private final WatchRepository watchRepository;
    private final SessionRepository sessionRepository;
    private Long idEvent;
    private ErrorCallback errorCallback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler, EventRepository eventRepository,
      WatchRepository watchRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.eventRepository = eventRepository;
        this.watchRepository = watchRepository;
        this.sessionRepository = sessionRepository;

        this.errorCallback = new ErrorCallback() {
            @Override public void onError(Throwable error) {
                interactorHandler.sendError(error);
            }
        };
    }

    public void selectEvent(Long idEvent) {
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        hideOldVisibleEvent();
        setNewVisibleEvent();
    }

    private void setNewVisibleEvent() {
        Watch selectedEventWatch =
          watchRepository.getWatchForUserAndEvent(sessionRepository.getCurrentUser(), idEvent, errorCallback);
        if (selectedEventWatch == null) {
            selectedEventWatch = createWatch();
        }
        selectedEventWatch.setVisible(true);

        watchRepository.putWatch(selectedEventWatch, new WatchRepository.WatchCallback() {
            @Override public void onLoaded(Watch watch) {
                interactorHandler.sendUiMessage(watch);
            }

            @Override public void onError(Throwable error) {
                interactorHandler.sendError(error);
            }
        });
    }

    private void hideOldVisibleEvent() {
        Event oldVisibleEvent = eventRepository.getVisibleEvent();
        if (oldVisibleEvent == null) {
            return;
        }
        Watch oldVisibleEventWatch =
          watchRepository.getWatchForUserAndEvent(sessionRepository.getCurrentUser(), oldVisibleEvent.getId(), errorCallback);
        oldVisibleEventWatch.setVisible(false);
        watchRepository.putWatch(oldVisibleEventWatch, new WatchRepository.WatchCallback() {
            @Override public void onLoaded(Watch watch) {
                /* no-op */
            }

            @Override public void onError(Throwable error) {
                errorCallback.onError(error);
            }
        });
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
