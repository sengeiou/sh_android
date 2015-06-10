package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalEventRepository implements EventRepository {

    private final EventDataSource localEventDataSource;
    private final EventSearchDataSource localEventSearchDataSource;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalEventRepository(@Local EventDataSource localEventDataSource,
      @Local EventSearchDataSource localEventSearchDataSource, EventEntityMapper eventEntityMapper) {
        this.localEventDataSource = localEventDataSource;
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getEventById(String idEvent) {
        EventEntity eventEntity = localEventDataSource.getEventById(idEvent);
        if (eventEntity == null) {
            eventEntity = fallbackOnSearchResults(idEvent);
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

    @Override public List<Event> getEventsByIds(List<Long> eventIds) {
        List<EventEntity> eventEntities = localEventDataSource.getEventsByIds(eventIds);
        return eventEntityMapper.transform(eventEntities);
    }

    @Override public Event putEvent(Event event) {
        EventEntity eventEntity = eventEntityMapper.transform(event);
        localEventDataSource.putEvent(eventEntity);
        return event;
    }

    @Override public Event putEvent(Event event, boolean notify) {
        throw new RuntimeException("Notify not allowed in local repository.");
    }

    @Override public Integer getEventsListingNumber(String idUser) {
        return localEventDataSource.getEventsListingNumber(idUser);
    }
}
