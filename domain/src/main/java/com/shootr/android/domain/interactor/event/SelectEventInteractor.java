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

    private Long idSelectedEvent;
    private Callback callback;

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

    public void selectEvent(Long idEvent, Callback callback) {
        this.idSelectedEvent = idEvent;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = sessionRepository.getCurrentUser();
        if (isSelectingCurrentVisibleEvent(currentUser)) {
            return;
        }
        Event selectedEvent = localEventRepository.getEventById(idSelectedEvent);

        User updatedUser = updateUserWithEventInfo(currentUser, selectedEvent);

        sessionRepository.setCurrentUser(updatedUser);
        localUserRepository.putUser(updatedUser);
        notifyLoaded(idSelectedEvent);
        remoteUserRepository.putUser(updatedUser);
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

    private void notifyLoaded(final Long selectedEventId) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(selectedEventId);
            }
        });
    }

    public interface Callback {

        void onLoaded(Long selectedEventId);
    }
}
