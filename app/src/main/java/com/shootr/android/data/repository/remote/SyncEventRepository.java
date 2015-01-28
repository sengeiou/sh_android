package com.shootr.android.data.repository.remote;

import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import javax.inject.Inject;

public class SyncEventRepository implements EventRepository, SyncableRepository {

    @Inject public SyncEventRepository() {
    }

    @Override public Event getVisibleEvent() {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Event getEventById(Long idEvent) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
