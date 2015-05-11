package com.shootr.android.data.repository.datasource.event;

import android.support.v4.util.ArrayMap;
import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.db.manager.EventManager;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseMemoryEventSearchDataSource implements EventSearchDataSource {

    private final EventManager eventManager;
    private final Map<String, EventSearchEntity> lastEventSearchResults;

    @Inject public DatabaseMemoryEventSearchDataSource(EventManager eventManager) {
        this.eventManager = eventManager;
        lastEventSearchResults = new ArrayMap<>();
    }

    public void setLastSearchResults(List<EventSearchEntity> eventSearchEntities) {
        lastEventSearchResults.clear();
        for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
            lastEventSearchResults.put(eventSearchEntity.getIdEvent(), eventSearchEntity);
        }
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

    @Override public EventSearchEntity getEventResult(String idEvent) {
        EventSearchEntity eventFromDefaultList = eventManager.getEventSearchResultById(idEvent);
        if (eventFromDefaultList != null) {
            return eventFromDefaultList;
        } else {
            EventSearchEntity eventFromLastSearchResults = lastEventSearchResults.get(idEvent);
            return eventFromLastSearchResults;
        }
    }
}
