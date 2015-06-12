package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class LocalEventSearchRepository implements EventSearchRepository {

    private final EventSearchDataSource localEventSearchDataSource;
    private final EventSearchEntityMapper eventSearchEntityMapper;
    private final EventDataSource localEventDataSource;
    private final WatchersRepository localWatchersRepository;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalEventSearchRepository(@Local EventSearchDataSource localEventSearchDataSource,
      EventSearchEntityMapper eventSearchEntityMapper, @Local EventDataSource localEventDataSource,
      @Local WatchersRepository localWatchersRepository, EventEntityMapper eventEntityMapper) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
        this.localEventDataSource = localEventDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents(String locale) {
        List<EventSearchEntity> defaultEvents = localEventSearchDataSource.getDefaultEvents(locale);
        return eventSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query, String locale) {
        List<EventSearchEntity> eventSearchEntities = localEventSearchDataSource.getEvents(query, locale);
        return eventSearchEntityMapper.transformToDomain(eventSearchEntities);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        localEventSearchDataSource.putDefaultEvents(eventSearchEntityMapper.transform(eventSearchResults));
    }

    @Override public void deleteDefaultEvents() {
        localEventSearchDataSource.deleteDefaultEvents();
    }

    @Override public List<EventSearchResult> getEventsListing(String idUser, String listingIdUser, String locale) {
        List<EventEntity> eventEntitiesListing = localEventDataSource.getEventsListing(idUser, listingIdUser,
          locale);
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
