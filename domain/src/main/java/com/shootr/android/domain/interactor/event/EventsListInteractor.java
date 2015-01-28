package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventSearchRepository eventSearchRepository;
    private final EventRepository eventRepository;

    @Inject public EventsListInteractor(InteractorHandler interactorHandler,
      EventSearchRepository eventSearchRepository, EventRepository eventRepository) {
        this.interactorHandler = interactorHandler;
        this.eventSearchRepository = eventSearchRepository;
        this.eventRepository = eventRepository;
    }

    public void loadEvents() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<EventSearchResult> events = eventSearchRepository.getDefaultEvents();
        EventSearchResultList eventSearchResultList = new EventSearchResultList(events);

        Event visibleEvent = eventRepository.getVisibleEvent();
        eventSearchResultList.setCurrentVisibleEvent(visibleEvent);

        interactorHandler.sendUiMessage(eventSearchResultList);
    }
}
