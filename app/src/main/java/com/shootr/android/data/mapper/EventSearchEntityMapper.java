package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

public class EventSearchEntityMapper {

    public final EventEntityMapper eventEntityMapper;

    @Inject public EventSearchEntityMapper(EventEntityMapper eventEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
    }

    public EventSearchResult transform(EventSearchEntity eventSearchEntity) {
        Event event = eventEntityMapper.transform(eventSearchEntity);

        EventSearchResult eventSearchResult = new EventSearchResult();
        eventSearchResult.setEvent(event);
        eventSearchResult.setWatchersNumber(eventSearchEntity.getWatchers());

        return eventSearchResult;
    }

    public List<EventSearchResult> transformToDomain(List<EventSearchEntity> eventSearchEntities) {
        List<EventSearchResult> eventSearchResults = new ArrayList<>(eventSearchEntities.size());
        for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
            eventSearchResults.add(transform(eventSearchEntity));
        }
        return eventSearchResults;
    }

    public EventSearchEntity transform(EventSearchResult eventSearchResult) {
        Event event = eventSearchResult.getEvent();

        EventSearchEntity eventSearchEntity = new EventSearchEntity();
        eventEntityMapper.transformToTemplate(event, eventSearchEntity);

        eventSearchEntity.setWatchers(eventSearchResult.getWatchersNumber());

        eventSearchEntity.setCsysBirth(new Date());
        eventSearchEntity.setCsysModified(new Date());

        return eventSearchEntity;
    }

    public List<EventSearchEntity> transform(List<EventSearchResult> eventSearchResults) {
        List<EventSearchEntity> entities = new ArrayList<>(eventSearchResults.size());
        for (EventSearchResult eventSearchResult : eventSearchResults) {
            entities.add(transform(eventSearchResult));
        }
        return entities;
    }

}
