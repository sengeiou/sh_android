package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalEventSearchRepository implements EventSearchRepository {

    private final EventSearchDataSource localEventSearchDataSource;
    private final EventSearchEntityMapper eventSearchEntityMapper;

    @Inject public LocalEventSearchRepository(@Local EventSearchDataSource localEventSearchDataSource,
      EventSearchEntityMapper eventSearchEntityMapper) {
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents() {
        List<EventSearchEntity> defaultEvents = localEventSearchDataSource.getDefaultEvents();
        return eventSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query) {
        List<EventSearchEntity> eventSearchEntities = localEventSearchDataSource.getEvents(query);
        return eventSearchEntityMapper.transformToDomain(eventSearchEntities);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        localEventSearchDataSource.putDefaultEvents(eventSearchEntityMapper.transform(eventSearchResults));
    }

    @Override public void deleteDefaultEvents() {
        localEventSearchDataSource.deleteDefaultEvents();
    }
}
