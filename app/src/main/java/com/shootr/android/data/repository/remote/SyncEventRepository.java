package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.RemoteDataSource;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import javax.inject.Inject;

public class SyncEventRepository implements EventRepository, SyncableRepository {

    private final EventEntityMapper eventEntityMapper;
    private final EventDataSource localEventDataSource;
    private final EventDataSource remoteEventDataSource;

    @Inject public SyncEventRepository(EventEntityMapper eventEntityMapper,
      @LocalDataSource EventDataSource localEventDataSource, @RemoteDataSource EventDataSource remoteEventDataSource) {
        this.localEventDataSource = localEventDataSource;
        this.remoteEventDataSource = remoteEventDataSource;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getEventById(Long idEvent) {
        EventEntity eventEntity = remoteEventDataSource.getEventById(idEvent);
        if (eventEntity != null) {
            localEventDataSource.putEvent(eventEntity);
            return eventEntityMapper.transform(eventEntity);
        } else {
            return null;
        }
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
