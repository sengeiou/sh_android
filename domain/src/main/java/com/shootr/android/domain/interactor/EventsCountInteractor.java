package com.shootr.android.domain.interactor;

import com.shootr.android.domain.repository.WatchRepository;
import javax.inject.Inject;

public class EventsCountInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final WatchRepository watchRepository;

    @Inject public EventsCountInteractor(InteractorHandler interactorHandler, WatchRepository watchRepository) {
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
