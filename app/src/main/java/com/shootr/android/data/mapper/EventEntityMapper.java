package com.shootr.android.data.mapper;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.domain.Event;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class EventEntityMapper {

    @Inject public EventEntityMapper() {
    }

    public Event transform(StreamEntity streamEntity) {
        if (streamEntity == null) {
            return null;
        }
        Event event = new Event();
        event.setId(streamEntity.getIdEvent());
        event.setAuthorId(streamEntity.getIdUser());
        event.setTitle(streamEntity.getTitle());
        event.setPicture(streamEntity.getPhoto());
        event.setTag(streamEntity.getTag());
        event.setAuthorUsername(streamEntity.getUserName());
        event.setLocale(streamEntity.getLocale());
        return event;
    }

    public List<Event> transform(List<StreamEntity> eventEntities) {
        List<Event> events = new ArrayList<>(eventEntities.size());
        for (StreamEntity streamEntity : eventEntities) {
            events.add(transform(streamEntity));
        }
        return events;
    }

    public StreamEntity transform(Event event) {
        StreamEntity streamEntity = new StreamEntity();
        transformToTemplate(event, streamEntity);
        return streamEntity;
    }

    protected void transformToTemplate(Event event, StreamEntity entityTemplate) {
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
