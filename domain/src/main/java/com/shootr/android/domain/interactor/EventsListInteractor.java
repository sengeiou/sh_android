package com.shootr.android.domain.interactor;

import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.repository.EventSearchRepository;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventSearchRepository eventSearchRepository;

    @Inject public EventsListInteractor(InteractorHandler interactorHandler, EventSearchRepository eventSearchRepository) {
        this.interactorHandler = interactorHandler;
        this.eventSearchRepository = eventSearchRepository;
    }

    public void loadEvents() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        eventSearchRepository.getDefaultEvents(new EventSearchRepository.EventResultListCallback() {
            @Override public void onLoaded(List<EventSearchResult> events) {
                interactorHandler.sendUiMessage(new EventSearchResultList(events));
            }

            @Override public void onError(Throwable error) {
                interactorHandler.sendError(error);
            }
        });
    }
}
