package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventSearchEntity;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
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

    public List<EventSearchResult> transform(List<EventSearchEntity> eventSearchEntities) {
        List<EventSearchResult> eventSearchResults = new ArrayList<>(eventSearchEntities.size());
        for (EventSearchEntity eventSearchEntity : eventSearchEntities) {
            eventSearchResults.add(transform(eventSearchEntity));
        }
        return eventSearchResults;
    }
}
