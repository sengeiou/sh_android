package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.User;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class EventNotificationInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final WatchRepository localWatchRepository;
    private final WatchRepository remoteWatchRepository;
    private final SessionRepository sessionRepository;
    private boolean enableNotifications;
    private Long idEvent;

    @Inject public EventNotificationInteractor(InteractorHandler interactorHandler, @Local WatchRepository localWatchRepository,
      @Remote WatchRepository remoteWatchRepository, SessionRepository sessionRepository) {
        this.interactorHandler = interactorHandler;
        this.localWatchRepository = localWatchRepository;
        this.remoteWatchRepository = remoteWatchRepository;
        this.sessionRepository = sessionRepository;
    }

    public void setNotificationEnabledForEvent(boolean enableNotifications, Long idEvent) {
        this.enableNotifications = enableNotifications;
        this.idEvent = idEvent;
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        User currentUser = sessionRepository.getCurrentUser();
        Watch currentWatch = localWatchRepository.getWatchForUserAndEvent(currentUser, idEvent);
        currentWatch.setNotificaticationsEnabled(enableNotifications);

        localWatchRepository.putWatch(currentWatch);
        remoteWatchRepository.putWatch(currentWatch);
    }
}
