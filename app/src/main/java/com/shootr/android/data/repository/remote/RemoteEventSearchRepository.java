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

    private List<EventSearchEntity> transformEventEntitiesInEventSearchEntities(List<EventEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<EventSearchEntity> results = new ArrayList<>(eventEntities.size());
        for (EventEntity eventEntity : eventEntities) {
            EventSearchEntity eventSearchEntity = transformEventEntityInEventSearchEntity(watchers, eventEntity);
            results.add(eventSearchEntity);
        }
        return results;
    }

    private EventSearchEntity transformEventEntityInEventSearchEntity(Map<String, Integer> watchers,
      EventEntity eventEntity) {
        EventSearchEntity eventSearchEntity = new EventSearchEntity();
        eventSearchEntity.setIdEvent(eventEntity.getIdEvent());
        eventSearchEntity.setIdUser(eventEntity.getIdUser());
        eventSearchEntity.setUserName(eventEntity.getUserName());
        eventSearchEntity.setTag(eventEntity.getTag());
        eventSearchEntity.setTitle(eventEntity.getTitle());
        eventSearchEntity.setPhoto(eventEntity.getPhoto());
        eventSearchEntity.setNotifyCreation(eventEntity.getNotifyCreation());
        eventSearchEntity.setLocale(eventEntity.getLocale());
        eventSearchEntity.setBirth(eventEntity.getBirth());
        eventSearchEntity.setDeleted(eventEntity.getDeleted());
        eventSearchEntity.setModified(eventEntity.getModified());
        eventEntity.setRevision(eventEntity.getRevision());
        if(watchers.get(eventEntity.getIdEvent()) != null) {
            eventSearchEntity.setWatchers(watchers.get(eventEntity.getIdEvent()));
        }
        return eventSearchEntity;
    }

    private List<EventSearchResult> addWatchersToEventSearchEntities(List<EventSearchEntity> eventSearchEntities,
      Map<String, Integer> watchers) {
        List<EventSearchResult> results = new ArrayList<>(eventSearchEntities.size());
        for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
            Integer eventWatchers = watchers.get(eventSearchEntity.getIdEvent());
            eventSearchEntity.setWatchers(eventWatchers != null ? eventWatchers : 0);
            EventSearchResult eventSearchResult = eventSearchEntityMapper.transform(eventSearchEntity);
            results.add(eventSearchResult);
        }
        return results;
    }

    @Override public List<EventSearchResult> getEvents(String query, String locale) {
        List<EventEntity> eventEntityList = remoteEventListDataSource.getEvents(query, locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();

        localEventSearchDataSource.setLastSearchResults(transformEventEntitiesInEventSearchEntities(eventEntityList,
          watchers));
        return transformEventEntitiesWithWatchers(eventEntityList, watchers);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public List<EventSearchResult> getEventsListing(String listingIdUser, String locale) {
        String currentUserId = sessionRepository.getCurrentUserId();
        List<EventEntity> eventEntitiesListing = remoteEventDataSource.getEventsListing(currentUserId, listingIdUser, locale);
        localEventDataSource.putEvents(eventEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
    }
}
