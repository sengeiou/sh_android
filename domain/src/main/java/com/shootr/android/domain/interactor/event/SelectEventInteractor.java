package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.User;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.UserRepository;
import com.shootr.android.domain.utils.TimeUtils;

import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;
    private final TimeUtils timeUtils;

    private String idSelectedEvent;
    private Callback<Event> callback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepository,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository,
      SessionRepository sessionRepository, TimeUtils timeUtils) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
        this.timeUtils = timeUtils;
    }
    //endregion

    public void selectEvent(String idEvent, Callback<Event> callback) {
        this.idSelectedEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = localUserRepository.getUserById(sessionRepository.getCurrentUserId());
        Event selectedEvent = localEventRepository.getEventById(idSelectedEvent);
        if (selectedEvent == null) {
            throw new RuntimeException(String.format("Event with id %s not found in local repository", idSelectedEvent));
        }

        if (isSelectingCurrentWatchingEvent(currentUser)) {
            notifyLoaded(selectedEvent);
        } else {
            User updatedUser = updateUserWithEventInfo(currentUser, selectedEvent);

            sessionRepository.setCurrentUser(updatedUser);
            localUserRepository.putUser(updatedUser);
            notifyLoaded(selectedEvent);
            remoteUserRepository.putUser(updatedUser);
        }
    }

    private boolean isSelectingCurrentWatchingEvent(User currentUser) {
        return idSelectedEvent.equals(currentUser.getIdWatchingEvent());
    }

    protected User updateUserWithEventInfo(User currentUser, Event selectedEvent) {
        currentUser.setIdWatchingEvent(selectedEvent.getId());
        currentUser.setWatchingEventTitle(selectedEvent.getTitle());
        currentUser.setJoinEventDate(getCurrentTime());
        return currentUser;
    }

    private long getCurrentTime() {
        return timeUtils.getCurrentTime();
    }

    private void notifyLoaded(final Event selectedEvent) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(selectedEvent);
            }
        });
    }
}
