package com.shootr.mobile.data;

import com.shootr.mobile.data.bus.Unauthorized;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.repository.SessionRepository;
import com.shootr.mobile.interactor.InteractorExecutor;
import com.shootr.mobile.util.DatabaseVersionUtils;

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
