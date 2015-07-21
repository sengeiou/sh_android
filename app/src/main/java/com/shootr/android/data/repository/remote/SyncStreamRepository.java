package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
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
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;
    private final SyncableEventEntityFactory syncableEventEntityFactory;

    @Inject public SyncStreamRepository(EventEntityMapper eventEntityMapper,
      @Local StreamDataSource localStreamDataSource, @Remote StreamDataSource remoteStreamDataSource,
      SyncableEventEntityFactory syncableEventEntityFactory) {
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
        this.eventEntityMapper = eventEntityMapper;
        this.syncableEventEntityFactory = syncableEventEntityFactory;
    }

    @Override public Event getStreamById(String idStream) {
        StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream);
        if (streamEntity != null) {
            markEntityAsSynchronized(streamEntity);
            localStreamDataSource.putStream(streamEntity);
            return eventEntityMapper.transform(streamEntity);
        } else {
            return null;
        }
    }

    @Override public List<Event> getStreamsByIds(List<String> streamIds) {
        List<StreamEntity> remoteEvents = remoteStreamDataSource.getStreamByIds(streamIds);
        markEntitiesAsSynchronized(remoteEvents);
        localStreamDataSource.putStreams(remoteEvents);
        return eventEntityMapper.transform(remoteEvents);
    }

    @Override public Event putStream(Event event) {
        return putStream(event, false);
    }

    @Override public Event putStream(Event event, boolean notify) {
        StreamEntity currentOrNewEntity = syncableEventEntityFactory.updatedOrNewEntity(event);
        currentOrNewEntity.setNotifyCreation(notify ? 1 : 0);

        StreamEntity remoteStreamEntity = remoteStreamDataSource.putStream(currentOrNewEntity);
        markEntityAsSynchronized(remoteStreamEntity);
        localStreamDataSource.putStream(remoteStreamEntity);
        return eventEntityMapper.transform(remoteStreamEntity);
    }

    @Override public Integer getListingCount(String idUser) {
        return remoteStreamDataSource.getListingCount(idUser);
    }

    @Override
    public void deleteStream(String idEvent) throws DeleteEventNotAllowedException {
        remoteStreamDataSource.deleteStream(idEvent);
    }

    private void markEntitiesAsSynchronized(List<StreamEntity> remoteEvents) {
        for (StreamEntity event : remoteEvents) {
            markEntityAsSynchronized(event);
        }
    }

    private void markEntityAsSynchronized(StreamEntity event) {
        event.setSynchronizedStatus(LocalSynchronized.SYNC_SYNCHRONIZED);
    }

    @Override public void dispatchSync() {
        throw new RuntimeException("Method not implemented yet!");
    }
}
