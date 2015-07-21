package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalStreamRepository implements StreamRepository {

    private final EventDataSource localEventDataSource;
    private final EventSearchDataSource localEventSearchDataSource;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalStreamRepository(@Local EventDataSource localEventDataSource,
      @Local EventSearchDataSource localEventSearchDataSource, EventEntityMapper eventEntityMapper) {
        this.localEventDataSource = localEventDataSource;
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getStreamById(String idStream) {
        EventEntity eventEntity = localEventDataSource.getEventById(idStream);
        if (eventEntity == null) {
            eventEntity = fallbackOnSearchResults(idStream);
        }
        return eventEntityMapper.transform(eventEntity);
    }

    private EventEntity fallbackOnSearchResults(String idEvent) {
        EventEntity eventEntity = localEventSearchDataSource.getEventResult(idEvent);
        if (eventEntity != null) {
            localEventDataSource.putEvent(eventEntity);
        }
        return eventEntity;
    }

    @Override public List<Event> getStreamsByIds(List<String> streamIds) {
        List<EventEntity> eventEntities = localEventDataSource.getEventsByIds(streamIds);
        return eventEntityMapper.transform(eventEntities);
    }

    @Override public Event putStream(Event event) {
        EventEntity eventEntity = eventEntityMapper.transform(event);
        localEventDataSource.putEvent(eventEntity);
        return event;
    }

    @Override public Event putStream(Event event, boolean notify) {
        throw new IllegalStateException("Notify not allowed in local repository.");
    }

    @Override public Integer getListingCount(String idUser) {
        return localEventDataSource.getListingCount(idUser);
    }

    @Override
    public void deleteStream(String idEvent) throws DeleteEventNotAllowedException {
        localEventDataSource.deleteEvent(idEvent);
    }
}
