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

public class WatchingInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final PostExecutionThread postExecutionThread;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;
    private final SessionRepository sessionRepository;

    private boolean isWatching;
    private Long idEvent;
    private String userStatus;
    private Callback callback;

    @Inject public WatchingInteractor(InteractorHandler interactorHandler, PostExecutionThread postExecutionThread,
      @Local WatchRepository localWatchRepository, @Remote WatchRepository remoteWatchRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.postExecutionThread = postExecutionThread;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.sessionRepository = sessionRepository;
    }

    public void sendWatching(boolean isWatching, Long idEvent, String userStatus, Callback callback) {
        this.isWatching = isWatching;
        this.idEvent = idEvent;
        this.userStatus = userStatus;
        this.callback = callback;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        if (isWatching) {
            removeOldWatch();
        }
        setNewWatch();
    }

    private void setNewWatch() {
        User currentUser = sessionRepository.getCurrentUser();

        Watch watch = new Watch();
        watch.setUser(currentUser);
        watch.setIdEvent(idEvent);
        watch.setWatching(isWatching);
        watch.setUserStatus(userStatus);
        watch.setVisible(true); //TODO what if watching is activated from a not visible event? OMG!
        watch.setNotificaticationsEnabled(notificationsEnabled());

        localWatchRepository.putWatch(watch);
        notifyLoaded(watch);
        remoteWatchRepository.putWatch(watch);
    }

    private void removeOldWatch() {
        Watch currentWatching = localWatchRepository.getCurrentWatching();
        if (currentWatching != null) {
            currentWatching.setWatching(false);
            currentWatching.setNotificaticationsEnabled(false);
            localWatchRepository.putWatch(currentWatching);
            remoteWatchRepository.putWatch(currentWatching);
        }
    }

    private boolean notificationsEnabled() {
        return isWatching;
    }

    private void notifyLoaded(final Watch watch) {
        postExecutionThread.post(new Runnable() {
            @Override public void run() {
                callback.onLoaded(watch);
            }
        });
    }

    public interface Callback {

        void onLoaded(Watch watchUpdated);

    }
}
