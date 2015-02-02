package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.manager.EventManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseEventDataSource implements EventDataSource {

    private final EventManager eventManager;

    @Inject public DatabaseEventDataSource(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override public EventEntity getEventById(Long idEvent) {
        return eventManager.getEventById(idEvent);
    }

    @Override public List<EventEntity> getEventsByIds(List<Long> eventIds) {
        return eventManager.getEventsByIds(eventIds);
    }

    @Override public EventEntity putEvent(EventEntity eventEntity) {
        eventManager.saveEvent(eventEntity);
        return eventEntity;
    }

    @Override public List<EventEntity> putEvents(List<EventEntity> events) {
        for (EventEntity event : events) {
            putEvent(event);
        }
        return events;
    }
}