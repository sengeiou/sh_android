package com.shootr.android.data.repository.local;

import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import javax.inject.Inject;

public class LocalEventRepository implements EventRepository {

    @Inject public LocalEventRepository() {
    }

    @Override public Event getVisibleEvent() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Event getEventById(Long idEvent) {
        throw new RuntimeException("Method not implemented yet!");
    }
}
