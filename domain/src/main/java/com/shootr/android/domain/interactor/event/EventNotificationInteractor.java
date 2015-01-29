package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.ErrorCallback;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class EventNotificationInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final WatchRepository watchRepository;
    private final SessionRepository sessionRepository;
    private boolean enableNotifications;
    private Long idEvent;

    @Inject public EventNotificationInteractor(InteractorHandler interactorHandler, WatchRepository watchRepository,
      SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.watchRepository = watchRepository;
        this.sessionRepository = sessionRepository;
    }

    public void setNotificationEnabledForEvent(boolean enableNotifications, Long idEvent) {
        this.enableNotifications = enableNotifications;
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = sessionRepository.getCurrentUser();
        Watch currentWatch = watchRepository.getWatchForUserAndEvent(currentUser, idEvent);
        currentWatch.setNotificaticationsEnabled(enableNotifications);

        watchRepository.putWatch(currentWatch, new WatchRepository.WatchCallback() {
            @Override public void onError(Throwable error) {
                interactorHandler.sendError(error);
            }

            @Override public void onLoaded(Watch watch) {
                /* no-op */
            }
        });
    }
}
