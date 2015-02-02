package com.shootr.android.domain.interactor.event;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.EventSearchResultList;
import com.shootr.android.domain.Watch;
import com.shootr.android.domain.interactor.Interactor;
import com.shootr.android.domain.interactor.InteractorHandler;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.WatchRepository;
import java.util.List;
import javax.inject.Inject;

public class EventsListInteractor implements Interactor {

    private final InteractorHandler interactorHandler;
    private final EventSearchRepository eventSearchRepository;
    private final EventRepository localEventRepository;
    private final WatchRepository localWatchRepository;

    @Inject public EventsListInteractor(InteractorHandler interactorHandler,
      EventSearchRepository eventSearchRepository, @Local EventRepository localEventRepository,
      @Local WatchRepository localWatchRepository) {
        this.interactorHandler = interactorHandler;
        this.eventSearchRepository = eventSearchRepository;
        this.localEventRepository = localEventRepository;
        this.localWatchRepository = localWatchRepository;
    }

    public void loadEvents() {
        interactorHandler.execute(this);
    }

    @Override public void execute() throws Throwable {
        List<EventSearchResult> events = eventSearchRepository.getDefaultEvents();
        EventSearchResultList eventSearchResultList = new EventSearchResultList(events);

        Watch visibleWatch = localWatchRepository.getCurrentVisibleWatch();
        if (visibleWatch != null) {
            Event visibleEvent = localEventRepository.getEventById(visibleWatch.getIdEvent());
            eventSearchResultList.setCurrentVisibleEvent(visibleEvent);
        }

        interactorHandler.sendUiMessage(eventSearchResultList);
    }
}
