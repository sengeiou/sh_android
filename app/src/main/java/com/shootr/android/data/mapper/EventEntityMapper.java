package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.Event;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventEntityMapper {

    @Inject public EventEntityMapper() {
    }

    public Event transform(EventEntity eventEntity) {
        if (eventEntity == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventEntity.getIdEvent());
        event.setTitle(eventEntity.getTitle());
        event.setStartDate(eventEntity.getBeginDate());
        event.setEndDate(eventEntity.getEndDate());
        event.setPicture(eventEntity.getPhoto());
        return event;
    }

    public List<Event> transform(List<EventEntity> eventEntities) {
        List<Event> events = new ArrayList<>(eventEntities.size());
        for (EventEntity eventEntity : eventEntities) {
            events.add(transform(eventEntity));
        }
        return events;
    }
}
