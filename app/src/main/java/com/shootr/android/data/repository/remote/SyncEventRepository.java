package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.LocalDataSource;
import com.shootr.android.data.repository.datasource.RemoteDataSource;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.repository.EventRepository;
import java.util.List;
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
            eventEntity.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
            localEventDataSource.putEvent(eventEntity);
            return eventEntityMapper.transform(eventEntity);
        } else {
            return null;
        }
    }

    @Override public List<Event> getEventsByIds(List<Long> eventIds) {
        List<EventEntity> remoteEvents = remoteEventDataSource.getEventsByIds(eventIds);
        markEntitiesAsSynchronized(remoteEvents);
        localEventDataSource.putEvents(remoteEvents);
        return eventEntityMapper.transform(remoteEvents);
    }

    private void markEntitiesAsSynchronized(List<EventEntity> remoteEvents) {
        for (EventEntity event : remoteEvents) {
            event.setCsysSynchronized(Synchronized.SYNC_SYNCHRONIZED);
        }
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
