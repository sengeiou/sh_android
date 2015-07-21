package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.DatabaseMemoryEventSearchDataSource;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.StreamListDataSource;
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
    private final StreamListDataSource remoteStreamListDataSource;
    private final WatchersRepository localWatchersRepository;
    private final EventEntityMapper eventEntityMapper;
    private final SessionRepository sessionRepository;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;

    @Inject public RemoteEventSearchRepository(DatabaseMemoryEventSearchDataSource localEventSearchDataSource,
      @Remote StreamListDataSource remoteStreamListDataSource, @Local WatchersRepository localWatchersRepository,
      EventEntityMapper eventEntityMapper, SessionRepository sessionRepository, @Local
    StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.remoteStreamListDataSource = remoteStreamListDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.eventEntityMapper = eventEntityMapper;
        this.sessionRepository = sessionRepository;
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
    }

    @Override public List<EventSearchResult> getDefaultEvents(String locale) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreamList(locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(streamEntityList, watchers);
    }

    private List<EventSearchResult> transformEventEntitiesWithWatchers(List<StreamEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<EventSearchResult> results = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            Event event = eventEntityMapper.transform(streamEntity);
            Integer eventWatchers = watchers.get(event.getId());
            EventSearchResult eventSearchResult =
              new EventSearchResult(event, eventWatchers != null ? eventWatchers : 0);
            results.add(eventSearchResult);
        }
        return results;
    }

    private List<StreamSearchEntity> transformEventEntitiesInEventSearchEntities(List<StreamEntity> eventEntities,
      Map<String, Integer> watchers) {
        List<StreamSearchEntity> results = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            StreamSearchEntity streamSearchEntity = transformEventEntityInEventSearchEntity(watchers, streamEntity);
            results.add(streamSearchEntity);
        }
        return results;
    }

    private StreamSearchEntity transformEventEntityInEventSearchEntity(Map<String, Integer> watchers,
      StreamEntity streamEntity) {
        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        streamSearchEntity.setIdEvent(streamEntity.getIdEvent());
        streamSearchEntity.setIdUser(streamEntity.getIdUser());
        streamSearchEntity.setUserName(streamEntity.getUserName());
        streamSearchEntity.setTag(streamEntity.getTag());
        streamSearchEntity.setTitle(streamEntity.getTitle());
        streamSearchEntity.setPhoto(streamEntity.getPhoto());
        streamSearchEntity.setNotifyCreation(streamEntity.getNotifyCreation());
        streamSearchEntity.setLocale(streamEntity.getLocale());
        streamSearchEntity.setBirth(streamEntity.getBirth());
        streamSearchEntity.setDeleted(streamEntity.getDeleted());
        streamSearchEntity.setModified(streamEntity.getModified());
        streamEntity.setRevision(streamEntity.getRevision());
        if(watchers.get(streamEntity.getIdEvent()) != null) {
            streamSearchEntity.setWatchers(watchers.get(streamEntity.getIdEvent()));
        }
        return streamSearchEntity;
    }

    @Override public List<EventSearchResult> getEvents(String query, String locale) {
        List<StreamEntity> streamEntityList = remoteStreamListDataSource.getStreams(query, locale);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();

        localEventSearchDataSource.setLastSearchResults(transformEventEntitiesInEventSearchEntities(streamEntityList,
          watchers));
        return transformEventEntitiesWithWatchers(streamEntityList, watchers);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public List<EventSearchResult> getEventsListing(String listingIdUser) {
        List<StreamEntity> eventEntitiesListing = remoteStreamDataSource.getStreamsListing(listingIdUser);
        localStreamDataSource.putStreams(eventEntitiesListing);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
    }
}
