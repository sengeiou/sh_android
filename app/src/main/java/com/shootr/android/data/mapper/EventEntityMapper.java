package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.LocalSynchronized;
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
        event.setAuthorId(eventEntity.getIdUser());
        event.setTitle(eventEntity.getTitle());
        event.setPicture(eventEntity.getPhoto());
        event.setTag(eventEntity.getTag());
        event.setAuthorUsername(eventEntity.getUserName());
        event.setLocale(eventEntity.getLocale());
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
        transformToTemplate(event, eventEntity);
        return eventEntity;
    }

    protected void transformToTemplate(Event event, EventEntity entityTemplate) {
        entityTemplate.setIdEvent(event.getId());
        entityTemplate.setIdUser(event.getAuthorId());
        entityTemplate.setTitle(event.getTitle());
        entityTemplate.setPhoto(event.getPicture());
        entityTemplate.setTag(event.getTag());
        entityTemplate.setUserName(event.getAuthorUsername());
        entityTemplate.setLocale(event.getLocale());

        entityTemplate.setSynchronizedStatus(LocalSynchronized.SYNC_NEW);
    }
}
