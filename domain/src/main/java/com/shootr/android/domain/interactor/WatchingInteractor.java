package com.shootr.android.domain.interactor;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class WatchingInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final WatchRepository watchRepository;
    private final SessionRepository sessionRepository;

    private boolean isWatching;
    private Long idEvent;
    private String userStatus;
    private WatchRepository.WatchCallback callback = new WatchRepository.WatchCallback() {
        @Override public void onError(Throwable error) {
            interactorHandler.sendError(error);
        }

        @Override public void onLoaded(Watch watch) {
            interactorHandler.sendUiMessage(watch);
        }
    };

    @Inject public WatchingInteractor(InteractorHandler interactorHandler, WatchRepository watchRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.watchRepository = watchRepository;
        this.sessionRepository = sessionRepository;
    }

    public void sendWatching(boolean isWatching, Long idEvent, String userStatus) {
        this.isWatching = isWatching;
        this.idEvent = idEvent;
        this.userStatus = userStatus;
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
        watch.setNotificaticationsEnabled(notificationsEnabled());

        watchRepository.putWatch(watch, callback);
    }

    private void removeOldWatch() {
        Watch currentWatching = watchRepository.getCurrentWatching(callback);
        if (currentWatching != null) {
            currentWatching.setWatching(false);
            currentWatching.setNotificaticationsEnabled(false);
            watchRepository.putWatch(currentWatching, callback);
        }
    }

    private boolean notificationsEnabled() {
        return isWatching;
    }
}
