package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.StreamSearchEntity;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

public class EventSearchEntityMapper {

    public final EventEntityMapper eventEntityMapper;

    @Inject public EventSearchEntityMapper(EventEntityMapper eventEntityMapper) {
        this.eventEntityMapper = eventEntityMapper;
    }

    public EventSearchResult transform(StreamSearchEntity streamSearchEntity) {
        Event event = eventEntityMapper.transform(streamSearchEntity);

        EventSearchResult eventSearchResult = new EventSearchResult();
        eventSearchResult.setEvent(event);
        eventSearchResult.setWatchersNumber(streamSearchEntity.getWatchers());

        return eventSearchResult;
    }

    public List<EventSearchResult> transformToDomain(List<StreamSearchEntity> eventSearchEntities) {
        List<EventSearchResult> eventSearchResults = new ArrayList<>(eventSearchEntities.size());
        for (StreamSearchEntity streamSearchEntity : eventSearchEntities) {
            eventSearchResults.add(transform(streamSearchEntity));
        }
        return eventSearchResults;
    }

    public StreamSearchEntity transform(EventSearchResult eventSearchResult) {
        Event event = eventSearchResult.getEvent();

        StreamSearchEntity streamSearchEntity = new StreamSearchEntity();
        eventEntityMapper.transformToTemplate(event, streamSearchEntity);

        streamSearchEntity.setWatchers(eventSearchResult.getWatchersNumber());

        streamSearchEntity.setBirth(new Date());
        streamSearchEntity.setModified(new Date());

        return streamSearchEntity;
    }

    public List<StreamSearchEntity> transform(List<EventSearchResult> eventSearchResults) {
        List<StreamSearchEntity> entities = new ArrayList<>(eventSearchResults.size());
        for (EventSearchResult eventSearchResult : eventSearchResults) {
            entities.add(transform(eventSearchResult));
        }
        return entities;
    }

}
