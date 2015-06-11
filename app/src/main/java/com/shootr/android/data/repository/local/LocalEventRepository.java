package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LocalEventRepository implements EventRepository {

    private final EventDataSource localEventDataSource;
    private final EventSearchDataSource localEventSearchDataSource;
    private final EventEntityMapper eventEntityMapper;
    private final WatchersRepository localWatchersRepository;

    @Inject public LocalEventRepository(@Local EventDataSource localEventDataSource,
      @Local EventSearchDataSource localEventSearchDataSource, EventEntityMapper eventEntityMapper,
      @Local WatchersRepository localWatchersRepository) {
        this.localEventDataSource = localEventDataSource;
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventEntityMapper = eventEntityMapper;
        this.localWatchersRepository = localWatchersRepository;
    }

    @Override public Event getEventById(String idEvent) {
        EventEntity eventEntity = localEventDataSource.getEventById(idEvent);
        if (eventEntity == null) {
            eventEntity = fallbackOnSearchResults(idEvent);
        }
        return eventEntityMapper.transform(eventEntity);
    }

    private EventEntity fallbackOnSearchResults(String idEvent) {
        EventEntity eventEntity = localEventSearchDataSource.getEventResult(idEvent);
        if (eventEntity != null) {
            localEventDataSource.putEvent(eventEntity);
        }
        return eventEntity;
    }

    @Override public List<Event> getEventsByIds(List<Long> eventIds) {
        List<EventEntity> eventEntities = localEventDataSource.getEventsByIds(eventIds);
        return eventEntityMapper.transform(eventEntities);
    }

    @Override public Event putEvent(Event event) {
        EventEntity eventEntity = eventEntityMapper.transform(event);
        localEventDataSource.putEvent(eventEntity);
        return event;
    }

    @Override public Event putEvent(Event event, boolean notify) {
        throw new RuntimeException("Notify not allowed in local repository.");
    }

    @Override public Integer getEventsListingNumber(String idUser) {
        return localEventDataSource.getEventsListingNumber(idUser);
    }

    @Override public List<EventSearchResult> getEventsListing(String idUser, String listingIdUser, String locale,
      Integer maxNumberOfListingEvents) {
        List<EventEntity> eventEntitiesListing = localEventDataSource.getEventsListing(idUser, listingIdUser,
          locale,
          maxNumberOfListingEvents);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
    }

    private List<EventSearchResult> transformEventEntitiesWithWatchers(List<EventEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<EventSearchResult> results = new ArrayList<>(eventEntities.size());
        for (EventEntity eventEntity : eventEntities) {
            Event event = eventEntityMapper.transform(eventEntity);
            Integer eventWatchers = watchers.get(event.getId());
            EventSearchResult eventSearchResult =
              new EventSearchResult(event, eventWatchers != null ? eventWatchers : 0);
            results.add(eventSearchResult);
        }
        return results;
    }
}
