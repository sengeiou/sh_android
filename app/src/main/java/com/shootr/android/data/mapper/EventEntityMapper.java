package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.Event;
import javax.inject.Inject;

public class EventEntityMapper {

    @Inject public EventEntityMapper() {
    }

    public Event transform(EventEntity eventEntity) {
        Event event = new Event();
        event.setId(eventEntity.getIdEvent());
        event.setTitle(eventEntity.getLocalTeamName()+"-"+ eventEntity.getVisitorTeamName());
        event.setStartDate(eventEntity.getBeginDate());
        //TODO endDate
        return event;
    }

}
