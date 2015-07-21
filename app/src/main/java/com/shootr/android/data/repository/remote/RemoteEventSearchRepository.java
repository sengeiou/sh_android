package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryEventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.EventListDataSource;
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

    @Deprecated private final DatabaseMemoryEventSearchDataSource localEventSearchDataSource;
    private final EventListDataSource remoteEventListDataSource;
    private final WatchersRepository localWatchersRepository;
    private final EventEntityMapper eventEntityMapper;
    private final SessionRepository sessionRepository;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;

    @Inject public RemoteEventSearchRepository(DatabaseMemoryEventSearchDataSource localEventSearchDataSource,
      @Remote EventListDataSource remoteEventListDataSource, @Local WatchersRepository localWatchersRepository,
      EventEntityMapper eventEntityMapper, SessionRepository sessionRepository, @Local
    StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.remoteEventListDataSource = remoteEventListDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.eventEntityMapper = eventEntityMapper;
        this.sessionRepository = sessionRepository;
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
    }

    @Override public List<EventSearchResult> getDefaultEvents(String locale) {
        List<EventEntity> eventEntityList = remoteEventListDataSource.getEventList(locale);
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

    @Override public List<EventSearchResult> getEventsListing(String listingIdUser) {
        List<EventEntity> eventEntitiesListing = remoteStreamDataSource.getStreamsListing(listingIdUser);
        localStreamDataSource.putStreams(eventEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
    }
}
