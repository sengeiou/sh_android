package com.shootr.android.data.repository.remote;

import com.shootr.android.data.entity.LocalSynchronized;
import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.mapper.StreamEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.sync.SyncableRepository;
import com.shootr.android.data.repository.sync.SyncableStreamEntityFactory;
import com.shootr.android.domain.Stream;
import com.shootr.android.domain.repository.Local;
import com.shootr.android.domain.repository.Remote;
import com.shootr.android.domain.repository.StreamRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncStreamRepository implements StreamRepository, SyncableRepository {

    private final StreamEntityMapper streamEntityMapper;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;
    private final SyncableStreamEntityFactory syncableStreamEntityFactory;

    @Inject public SyncStreamRepository(StreamEntityMapper streamEntityMapper,
      @Local StreamDataSource localStreamDataSource, @Remote StreamDataSource remoteStreamDataSource,
      SyncableStreamEntityFactory syncableStreamEntityFactory) {
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
        this.streamEntityMapper = streamEntityMapper;
        this.syncableStreamEntityFactory = syncableStreamEntityFactory;
    }

    @Override public Stream getStreamById(String idStream) {
        StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream);
        if (streamEntity != null) {
            markEntityAsSynchronized(streamEntity);
            localStreamDataSource.putStream(streamEntity);
            Stream stream = streamEntityMapper.transform(streamEntity);
            return stream;
        } else {
            return null;
        }
    }

    @Override public List<Stream> getStreamsByIds(List<String> streamIds) {
        List<StreamEntity> remoteEvents = remoteStreamDataSource.getStreamByIds(streamIds);
        markEntitiesAsSynchronized(remoteEvents);
        localStreamDataSource.putStreams(remoteEvents);
        return streamEntityMapper.transform(remoteEvents);
    }

    @Override public Stream putStream(Stream stream) {
        return putStream(stream, false);
    }

    @Override public Stream putStream(Stream stream, boolean notify) {
        StreamEntity currentOrNewEntity = syncableStreamEntityFactory.updatedOrNewEntity(stream);
        currentOrNewEntity.setNotifyCreation(notify ? 1 : 0);

        StreamEntity remoteStreamEntity = remoteStreamDataSource.putStream(currentOrNewEntity);
        markEntityAsSynchronized(remoteStreamEntity);
        localStreamDataSource.putStream(remoteStreamEntity);
        return streamEntityMapper.transform(remoteStreamEntity);
    }

    @Override public Integer getListingCount(String idUser) {
        return remoteStreamDataSource.getListingCount(idUser);
    }

    @Override public void shareStream(String idStream) {
        remoteStreamDataSource.shareStream(idStream);
    }

    @Override public void removeStream(String idStream) {
        remoteStreamDataSource.removeStream(idStream);
        StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream);
        markEntityAsSynchronized(streamEntity);
        localStreamDataSource.putStream(streamEntity);
    }

    @Override public void restoreStream(String idStream) {
        remoteStreamDataSource.restoreStream(idStream);
        StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream);
        markEntityAsSynchronized(streamEntity);
        localStreamDataSource.putStream(streamEntity);
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
