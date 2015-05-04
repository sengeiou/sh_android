package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalEventRepository implements EventRepository {

    private final EventDataSource localEventDataSource;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalEventRepository(@Local EventDataSource localEventDataSource, EventEntityMapper eventEntityMapper) {
        this.localEventDataSource = localEventDataSource;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getEventById(String idEvent) {
        EventEntity eventEntity = localEventDataSource.getEventById(idEvent);
        return eventEntityMapper.transform(eventEntity);
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
}
