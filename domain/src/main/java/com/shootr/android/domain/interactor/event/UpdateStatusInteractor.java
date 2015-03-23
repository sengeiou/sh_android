package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.executor.PostExecutionThread;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class UpdateStatusInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;
    private final SessionRepository sessionRepository;

    private Long idEvent;
    private String userStatus;
    private Callback callback;

    @Inject public UpdateStatusInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local WatchRepository localWatchRepository, @Remote WatchRepository remoteWatchRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.sessionRepository = sessionRepository;
    }

    public void sendWatching(Long idEvent, String userStatus, Callback callback) {
        this.idEvent = idEvent;
        this.userStatus = userStatus;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        setNewWatch();
    }

    private void setNewWatch() {
        User currentUser = sessionRepository.getCurrentUser();

        User watch = new Watch();
        watch.setUser(currentUser);
        watch.setIdEvent(idEvent);
        watch.setUserStatus(userStatus);
        watch.setVisible(true); //TODO what if watching is activated from a not visible event? OMG!

        localWatchRepository.putWatch(watch);
        notifyLoaded(watch);
        remoteWatchRepository.putWatch(watch);
    }

    private void notifyLoaded(final User watch) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(watch);
            }
        });
    }

    public interface Callback {

        void onLoaded(User watchUpdated);

    }
}
