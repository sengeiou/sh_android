package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.manager.EventManager;
import javax.inject.Inject;

public class DatabaseEventDataSource implements EventDataSource {

    private final EventManager eventManager;

    @Inject public DatabaseEventDataSource(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override public EventEntity getEventById(Long idEvent) {
        return eventManager.getEventById(idEvent);
    }

    @Override public EventEntity putEvent(EventEntity eventEntity) {
        eventManager.saveEvent(eventEntity);
        return eventEntity;
    }
}
