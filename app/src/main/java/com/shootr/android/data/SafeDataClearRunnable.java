package com.shootr.android.data;

import com.shootr.android.data.bus.Unauthorized;
import com.shootr.android.domain.bus.BusPublisher;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.interactor.InteractorExecutor;
import com.shootr.android.util.DatabaseVersionUtils;
import javax.inject.Inject;

public class SafeDataClearRunnable implements Runnable {

    private final InteractorExecutor interactorHandler;
    private final DatabaseVersionUtils databaseUtils;
    private final SessionRepository sessionRepository;
    private final BusPublisher busPublisher;

    @Inject
    public SafeDataClearRunnable(InteractorExecutor interactorHandler,
      DatabaseVersionUtils databaseUtils,
      SessionRepository sessionRepository,
      BusPublisher busPublisher) {
        this.interactorHandler = interactorHandler;
        this.databaseUtils = databaseUtils;
        this.sessionRepository = sessionRepository;
        this.busPublisher = busPublisher;
    }

    @Override
    public void run() {
        interactorHandler.stopInteractors();
        databaseUtils.clearDataOnLogout();
        sessionRepository.destroySession();
        busPublisher.post(new Unauthorized.Event());
    }
}
