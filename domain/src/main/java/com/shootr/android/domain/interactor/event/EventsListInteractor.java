package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventSearchRepository eventSearchRepository;
    private final SessionRepository sessionRepository;
    private final EventRepository localEventRepository;

    @Inject public EventsListInteractor(InteractorHandler interactorHandler,
      EventSearchRepository eventSearchRepository, SessionRepository sessionRepository,
      @Local EventRepository localEventRepository) {
        this.interactorHandler = interactorHandler;
        this.eventSearchRepository = eventSearchRepository;
        this.sessionRepository = sessionRepository;
        this.localEventRepository = localEventRepository;
    }

    public void loadEvents() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<EventSearchResult> events = eventSearchRepository.getDefaultEvents();
        EventSearchResultList eventSearchResultList = new EventSearchResultList(events);

        String eventWatchingId = sessionRepository.getCurrentUser().getVisibleEventId();
        if (eventWatchingId != "null") {
            Event visibleEvent = localEventRepository.getEventById(Long.valueOf(eventWatchingId));
            if (visibleEvent != null) {
                eventSearchResultList.setCurrentVisibleEvent(visibleEvent);
            }
        }

        interactorHandler.sendUiMessage(eventSearchResultList);
    }
}
