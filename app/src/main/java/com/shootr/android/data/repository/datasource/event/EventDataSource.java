package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import java.util.List;

public interface EventDataSource {

    EventEntity getEventById(String idEvent);

    List<EventEntity> getEventsByIds(List<String> eventIds);

    EventEntity putEvent(EventEntity eventEntity);

    List<EventEntity> putEvents(List<EventEntity> events);

    Integer getListingCount(String idUser);

    List<EventEntity> getEventsListing(String idUser);

    void deleteEvent(String idEvent) throws DeleteEventNotAllowedException;
}
