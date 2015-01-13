package com.shootr.android.domain.interactor;

import com.shootr.android.domain.EventInfo;
import com.shootr.android.domain.repository.EventInfoRepository;
import javax.inject.Inject;

public class VisibleEventInfoInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventInfoRepository eventInfoRepository;

    @Inject public VisibleEventInfoInteractor(InteractorHandler interactorHandler, EventInfoRepository eventInfoRepository) {
        this.interactorHandler = interactorHandler;
        this.eventInfoRepository = eventInfoRepository;
    }

    public void obtainEventInfo() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        eventInfoRepository.loadVisibleEventInfo(new EventInfoRepository.EventInfoCallback() {
            @Override public void onLoaded(EventInfo eventInfo) {
                interactorHandler.sendUiMessage(eventInfo);
            }
        });
    }

}
