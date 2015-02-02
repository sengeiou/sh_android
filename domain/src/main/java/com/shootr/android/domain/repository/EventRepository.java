package com.shootr.android.domain.repository;

import com.shootr.android.domain.Event;
import java.util.Collection;
import java.util.List;

public interface EventRepository {

    Event getEventById(Long idEvent);

    List<Event> getEventsByIds(List<Long> eventIds);
}
