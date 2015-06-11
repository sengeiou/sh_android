package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryEventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventListDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.SessionRepository;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class RemoteEventSearchRepository implements EventSearchRepository {

    @Deprecated private final EventSearchDataSource remoteEventSearchDataSource;
    @Deprecated private final DatabaseMemoryEventSearchDataSource localEventSearchDataSource;
    @Deprecated private final EventSearchEntityMapper eventSearchEntityMapper;
    private final EventListDataSource remoteEventListDataSource;
    private final WatchersRepository localWatchersRepository;
    private final EventEntityMapper eventEntityMapper;
    private final SessionRepository sessionRepository;
    private final EventDataSource localEventDataSource;
    private final EventDataSource remoteEventDataSource;

    @Inject public RemoteEventSearchRepository(@Remote EventSearchDataSource remoteEventSearchDataSource,
      DatabaseMemoryEventSearchDataSource localEventSearchDataSource, EventSearchEntityMapper eventSearchEntityMapper,
      @Remote EventListDataSource remoteEventListDataSource, @Local WatchersRepository localWatchersRepository,
      EventEntityMapper eventEntityMapper, SessionRepository sessionRepository, @Local EventDataSource localEventDataSource,
      @Remote EventDataSource remoteEventDataSource) {
        this.remoteEventSearchDataSource = remoteEventSearchDataSource;
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
        this.remoteEventListDataSource = remoteEventListDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.eventEntityMapper = eventEntityMapper;
        this.sessionRepository = sessionRepository;
        this.localEventDataSource = localEventDataSource;
        this.remoteEventDataSource = remoteEventDataSource;
    }

    @Override public List<EventSearchResult> getDefaultEvents(String locale) {
        List<EventEntity> eventEntityList = remoteEventListDataSource.getEventList(sessionRepository.getCurrentUserId(), locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntityList, watchers);
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

    @Override public List<EventSearchResult> getEvents(String query, String locale) {
        List<EventSearchEntity> eventSearchEntities = remoteEventSearchDataSource.getEvents(query, locale);
        localEventSearchDataSource.setLastSearchResults(eventSearchEntities);
        return eventSearchEntityMapper.transformToDomain(eventSearchEntities);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public List<EventSearchResult> getEventsListing(String idUser, String listingIdUser, String locale,
      Integer maxNumberOfListingEvents) {
        List<EventEntity> eventEntitiesListing = remoteEventDataSource.getEventsListing(idUser, listingIdUser, locale, maxNumberOfListingEvents);
        localEventDataSource.putEvents(eventEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
    }
}
