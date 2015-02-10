package com.shootr.android.data.repository;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.Cached;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final EventSearchDataSource cachedEventSearchDataSource;
    private final EventDataSource localEventDataSource;
    private final EventSearchEntityMapper eventSearchEntityMapper;

    @Inject public EventSearchRepositoryImpl(@Cached EventSearchDataSource cachedEventSearchDataSource,
      @Local EventDataSource localEventDataSource, EventSearchEntityMapper eventSearchEntityMapper) {
        this.cachedEventSearchDataSource = cachedEventSearchDataSource;
        this.localEventDataSource = localEventDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents() {
        List<EventSearchEntity> defaultEvents = cachedEventSearchDataSource.getDefaultEvents();
        saveEvents(defaultEvents);
        return eventSearchEntityMapper.transform(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query) {
        List<EventSearchEntity> eventSearchEntities = cachedEventSearchDataSource.getEvents(query);
        saveEvents(eventSearchEntities);
        return eventSearchEntityMapper.transform(eventSearchEntities);
    }

    private void saveEvents(List<EventSearchEntity> eventResults) {
        List<EventEntity> eventEntities = new ArrayList<>(eventResults.size());
        for (EventSearchEntity eventResult : eventResults) {
            eventEntities.add(eventResult);
        }
        localEventDataSource.putEvents(eventEntities);
    }
}
