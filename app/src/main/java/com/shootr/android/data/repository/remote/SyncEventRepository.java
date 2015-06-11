package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.Synchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.sync.SyncableEventEntityFactory;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.EventSearchResult;
import com.shootr.android.domain.repository.EventRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.WatchersRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

public class SyncEventRepository implements EventRepository, SyncableRepository {

    private final EventEntityMapper eventEntityMapper;
    private final EventDataSource localEventDataSource;
    private final EventDataSource remoteEventDataSource;
    private final SyncableEventEntityFactory syncableEventEntityFactory;

    @Inject public SyncEventRepository(EventEntityMapper eventEntityMapper, @Local EventDataSource localEventDataSource,
      @Remote EventDataSource remoteEventDataSource, SyncableEventEntityFactory syncableEventEntityFactory) {
        this.localEventDataSource = localEventDataSource;
        this.remoteEventDataSource = remoteEventDataSource;
        this.eventEntityMapper = eventEntityMapper;
        this.syncableEventEntityFactory = syncableEventEntityFactory;
    }

    @Override public Event getEventById(String idEvent) {
        EventEntity eventEntity = remoteEventDataSource.getEventById(idEvent);
        if (eventEntity != null) {
            markEntityAsSynchronized(eventEntity);
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

    @Override public Event putEvent(Event event) {
        return putEvent(event, false);
    }

    @Override public Event putEvent(Event event, boolean notify) {
        EventEntity currentOrNewEntity = syncableEventEntityFactory.currentOrNewEntity(event);
        currentOrNewEntity.setNotifyCreation(notify ? 1 : 0);

        EventEntity remoteEventEntity = remoteEventDataSource.putEvent(currentOrNewEntity);
        markEntityAsSynchronized(remoteEventEntity);
        localEventDataSource.putEvent(remoteEventEntity);
        return eventEntityMapper.transform(remoteEventEntity);
    }

    @Override public Integer getEventsListingNumber(String idUser) {
        return remoteEventDataSource.getEventsListingNumber(idUser);
    }

    private void markEntitiesAsSynchronized(List<EventEntity> remoteEvents) {
        for (EventEntity event : remoteEvents) {
            markEntityAsSynchronized(event);
        }
    }

    private void markEntityAsSynchronized(EventEntity event) {
        event.setSynchronizedStatus(Synchronized.SYNC_SYNCHRONIZED);
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
