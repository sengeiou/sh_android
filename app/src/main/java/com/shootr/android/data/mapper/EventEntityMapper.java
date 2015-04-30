package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.Synchronized;
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
        event.setId(String.valueOf(eventEntity.getIdEvent()));
        event.setAuthorId(String.valueOf(eventEntity.getIdUser()));
        event.setTitle(eventEntity.getTitle());
        event.setStartDate(eventEntity.getBeginDate());
        event.setEndDate(eventEntity.getEndDate());
        event.setPicture(eventEntity.getPhoto());
        event.setTimezone(eventEntity.getTimezone());
        event.setTag(eventEntity.getTag());
        event.setAuthorUsername(eventEntity.getUserName());
        return event;
    }

    public List<Event> transform(List<EventEntity> eventEntities) {
        List<Event> events = new ArrayList<>(eventEntities.size());
        for (EventEntity eventEntity : eventEntities) {
            events.add(transform(eventEntity));
        }
        return events;
    }

    public EventEntity transform(Event event) {
        EventEntity eventEntity = new EventEntity();
        eventEntity.setIdEvent(Long.valueOf(event.getId()));
        eventEntity.setIdUser(Long.valueOf(event.getAuthorId()));
        eventEntity.setTitle(event.getTitle());
        eventEntity.setBeginDate(event.getStartDate());
        eventEntity.setEndDate(event.getEndDate());
        eventEntity.setPhoto(event.getPicture());
        eventEntity.setTimezone(event.getTimezone());
        eventEntity.setTag(event.getTag());
        eventEntity.setUserName(event.getAuthorUsername());

        eventEntity.setCsysSynchronized(Synchronized.SYNC_NEW);
        return eventEntity;
    }

}
