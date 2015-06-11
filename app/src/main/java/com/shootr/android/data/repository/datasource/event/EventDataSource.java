package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.Event;
import java.util.List;

public interface EventDataSource {

    EventEntity getEventById(String idEvent);

    List<EventEntity> getEventsByIds(List<Long> eventIds);

    EventEntity putEvent(EventEntity eventEntity);

    List<EventEntity> putEvents(List<EventEntity> events);

    Integer getEventsListingNumber(String idUser);

    List<EventEntity> getEventsListing(String me, String idUser, String locale,
      Integer maxNumberOfListingEvents);
}
