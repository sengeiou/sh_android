package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.data.mapper.EventSearchEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventSearchRepository;
import com.shootr.android.domain.repository.Remote;

import java.util.List;

import javax.inject.Inject;

public class RemoteEventSearchRepository implements EventSearchRepository {

    private final EventSearchDataSource remoteEventSearchDataSource;
    private final EventSearchEntityMapper eventSearchEntityMapper;

    @Inject public RemoteEventSearchRepository(@Remote EventSearchDataSource remoteEventSearchDataSource,
      EventSearchEntityMapper eventSearchEntityMapper) {
        this.remoteEventSearchDataSource = remoteEventSearchDataSource;
        this.eventSearchEntityMapper = eventSearchEntityMapper;
    }

    @Override public List<EventSearchResult> getDefaultEvents() {
        List<EventSearchEntity> defaultEvents = remoteEventSearchDataSource.getDefaultEvents();
        return eventSearchEntityMapper.transformToDomain(defaultEvents);
    }

    @Override public List<EventSearchResult> getEvents(String query) {
        List<EventSearchEntity> eventSearchEntities = remoteEventSearchDataSource.getEvents(query);
        return eventSearchEntityMapper.transformToDomain(eventSearchEntities);
    }

    @Override public void putDefaultEvents(List<EventSearchResult> eventSearchResults) {
        throw new IllegalStateException("Method not implemented in remote repository");
    }

    @Override public void deleteDefaultEvents() {
        throw new IllegalStateException("Method not implemented in remote repository");
    }
}
