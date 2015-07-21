package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
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
    private final StreamDataSource localStreamDataSource;
    private final WatchersRepository localWatchersRepository;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalEventSearchRepository(@Local EventSearchDataSource localEventSearchDataSource,
      EventSearchEntityMapper eventSearchEntityMapper, @Local StreamDataSource localStreamDataSource,
      @Local WatchersRepository localWatchersRepository, EventEntityMapper eventEntityMapper) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
        this.localStreamDataSource = localStreamDataSource;
        this.localWatchersRepository = localWatchersRepository;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents(String locale) {
        List<StreamSearchEntity> defaultEvents = localEventSearchDataSource.getDefaultEvents(locale);
        return eventSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query, String locale) {
        throw new IllegalArgumentException("method not implemented in local repository");
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        localEventSearchDataSource.putDefaultEvents(eventSearchEntityMapper.transform(eventSearchResults));
    }

    @Override public void deleteDefaultEvents() {
        localEventSearchDataSource.deleteDefaultEvents();
    }

    @Override public List<EventSearchResult> getEventsListing(String listingIdUser) {
        List<StreamEntity> eventEntitiesListing = localStreamDataSource.getStreamsListing(listingIdUser);
        Map<String, Integer> watchers = localWatchersRepository.getWatchers();
        return transformEventEntitiesWithWatchers(eventEntitiesListing, watchers);
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
}
