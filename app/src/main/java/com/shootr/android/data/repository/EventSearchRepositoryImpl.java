package com.shootr.android.data.repository;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.Cached;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class EventSearchRepositoryImpl implements EventSearchRepository {

    private final EventSearchDataSource cachedEventSearchDataSource;
    private final EventSearchEntityMapper eventSearchEntityMapper;

    @Inject public EventSearchRepositoryImpl(@Cached EventSearchDataSource cachedEventSearchDataSource,
      EventSearchEntityMapper eventSearchEntityMapper) {
        this.cachedEventSearchDataSource = cachedEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents() {
        List<EventSearchEntity> defaultEvents = cachedEventSearchDataSource.getDefaultEvents();
        return eventSearchEntityMapper.transform(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query) {
        List<EventSearchEntity> eventSearchEntities = cachedEventSearchDataSource.getEvents(query);
        return eventSearchEntityMapper.transform(eventSearchEntities);
    }
}
