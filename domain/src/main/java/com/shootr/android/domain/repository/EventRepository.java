package com.shootr.android.domain.repository;

import com.shootr.android.domain.Event;

public interface EventRepository {

    Event getEventById(Long idEvent);
}
