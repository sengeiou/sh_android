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
import javax.inject.Inject;

public class SelectEventInteractor implements Interactor {

    //region Dependencies
    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final EventRepository localEventRepository;
    private final UserRepository localUserRepository;
    private final UserRepository remoteUserRepository;
    private final SessionRepository sessionRepository;

    private String idSelectedEvent;
    private Callback<Event> callback;

    @Inject public SelectEventInteractor(final InteractorHandler interactorHandler,
      PostExecutionThread postExecutionThread, @Local EventRepository localEventRepository,
      @Local UserRepository localUserRepository, @Remote UserRepository remoteUserRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localEventRepository = localEventRepository;
        this.localUserRepository = localUserRepository;
        this.remoteUserRepository = remoteUserRepository;
        this.sessionRepository = sessionRepository;
    }
    //endregion

    public void selectEvent(String idEvent, Callback<Event> callback) {
        this.idSelectedEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = sessionRepository.getCurrentUser();
        Event selectedEvent = localEventRepository.getEventById(idSelectedEvent);

        if (isSelectingCurrentVisibleEvent(currentUser)) {
            notifyLoaded(selectedEvent);
        } else {
            User updatedUser = updateUserWithEventInfo(currentUser, selectedEvent);

            deleteCheckin(updatedUser);

            sessionRepository.setCurrentUser(updatedUser);
            localUserRepository.putUser(updatedUser);
            notifyLoaded(selectedEvent);
            remoteUserRepository.putUser(updatedUser);
        }
    }

    private void deleteCheckin(User updatedUser) {
        updatedUser.setCheckedIn(false);
    }

    private boolean isSelectingCurrentVisibleEvent(User currentUser) {
        return idSelectedEvent.equals(currentUser.getVisibleEventId());
    }

    protected User updateUserWithEventInfo(User currentUser, Event selectedEvent) {
        currentUser.setVisibleEventId(selectedEvent.getId());
        currentUser.setVisibleEventTitle(selectedEvent.getTitle());
        currentUser.setStatus("Watching"); //TODO hardoced, eh??
        return currentUser;
    }

    private void notifyLoaded(final Event selectedEvent) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(selectedEvent);
            }
        });
    }
}
