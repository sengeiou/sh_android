package com.shootr.android.domain.repository;

import com.shootr.android.domain.Event;

import com.shootr.android.domain.EventSearchResult;
import java.util.List;

public interface EventRepository {

    Event getEventById(String idEvent);

    List<Event> getEventsByIds(List<String> eventIds);

    Event putEvent(Event event);

    Event putEvent(Event event, boolean notify);

    Integer getListingCount(String idUser);
}
