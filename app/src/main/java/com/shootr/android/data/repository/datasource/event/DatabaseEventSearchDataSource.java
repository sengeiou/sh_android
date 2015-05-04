package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.domain.repository.SessionRepository;
import java.util.List;
import javax.inject.Inject;

public class DatabaseEventSearchDataSource implements EventSearchDataSource {

    private final EventManager eventManager;

    @Inject public DatabaseEventSearchDataSource(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override public List<EventSearchEntity> getDefaultEvents() {
        return eventManager.getDefaultEventSearch();
    }

    @Override public List<EventSearchEntity> getEvents(String query) {
        throw new IllegalStateException("Search not implemented in local datasource");
    }

    @Override public void putDefaultEvents(List<EventSearchEntity> eventSearchEntities) {
        eventManager.putDefaultEventSearch(eventSearchEntities);
    }

    @Override public void deleteDefaultEvents() {
        eventManager.deleteDefaultEventSearch();
    }

}
