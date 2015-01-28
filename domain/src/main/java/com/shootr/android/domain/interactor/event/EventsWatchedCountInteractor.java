package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class EventsWatchedCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final WatchRepository watchRepository;

    @Inject public EventsWatchedCountInteractor(InteractorHandler interactorHandler, WatchRepository watchRepository) {
        this.interactorHandler = interactorHandler;
        this.watchRepository = watchRepository;
    }

    public void obtainEventsCount() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        Integer allWatchesCount = watchRepository.getAllWatchesCount();
        interactorHandler.sendUiMessage(allWatchesCount);
    }
}
