package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.EventDataSource;
import com.shootr.android.data.repository.sync.SyncableEventEntityFactory;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import java.util.List;
import javax.inject.Inject;

public class SyncStreamRepository implements StreamRepository, SyncableRepository {

    private final EventEntityMapper eventEntityMapper;
    private final EventDataSource localEventDataSource;
    private final EventDataSource remoteEventDataSource;
    private final SyncableEventEntityFactory syncableEventEntityFactory;

    @Inject public SyncStreamRepository(EventEntityMapper eventEntityMapper,
      @Local EventDataSource localEventDataSource, @Remote EventDataSource remoteEventDataSource,
      SyncableEventEntityFactory syncableEventEntityFactory) {
        this.localEventDataSource = localEventDataSource;
        this.remoteEventDataSource = remoteEventDataSource;
        this.eventEntityMapper = eventEntityMapper;
        this.syncableEventEntityFactory = syncableEventEntityFactory;
    }

    @Override public Event getStreamById(String idStream) {
        EventEntity eventEntity = remoteEventDataSource.getEventById(idStream);
        if (eventEntity != null) {
            markEntityAsSynchronized(eventEntity);
            localEventDataSource.putEvent(eventEntity);
            return eventEntityMapper.transform(eventEntity);
        } else {
            return null;
        }
    }

    @Override public List<Event> getStreamsByIds(List<String> streamIds) {
        List<EventEntity> remoteEvents = remoteEventDataSource.getEventsByIds(streamIds);
        markEntitiesAsSynchronized(remoteEvents);
        localEventDataSource.putEvents(remoteEvents);
        return eventEntityMapper.transform(remoteEvents);
    }

    @Override public Event putStream(Event event) {
        return putStream(event, false);
    }

    @Override public Event putStream(Event event, boolean notify) {
        EventEntity currentOrNewEntity = syncableEventEntityFactory.updatedOrNewEntity(event);
        currentOrNewEntity.setNotifyCreation(notify ? 1 : 0);

        EventEntity remoteEventEntity = remoteEventDataSource.putEvent(currentOrNewEntity);
        markEntityAsSynchronized(remoteEventEntity);
        localEventDataSource.putEvent(remoteEventEntity);
        return eventEntityMapper.transform(remoteEventEntity);
    }

    @Override public Integer getListingCount(String idUser) {
        return remoteEventDataSource.getListingCount(idUser);
    }

    @Override
    public void deleteStream(String idEvent) throws DeleteEventNotAllowedException {
        remoteEventDataSource.deleteEvent(idEvent);
    }

    private void markEntitiesAsSynchronized(List<EventEntity> remoteEvents) {
        for (EventEntity event : remoteEvents) {
            markEntityAsSynchronized(event);
        }
    }

    private void markEntityAsSynchronized(EventEntity event) {
        event.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
