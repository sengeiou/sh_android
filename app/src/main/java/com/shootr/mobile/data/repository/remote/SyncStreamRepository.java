package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.LocalSynchronized;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.StreamDataSource;
import com.shootr.mobile.data.repository.remote.cache.StreamCache;
import com.shootr.mobile.data.repository.sync.SyncableRepository;
import com.shootr.mobile.data.repository.sync.SyncableStreamEntityFactory;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamUpdateParameters;
import com.shootr.mobile.domain.repository.Local;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.stream.ExternalStreamRepository;
import com.shootr.mobile.domain.repository.stream.StreamRepository;
import java.util.List;
import javax.inject.Inject;

public class SyncStreamRepository implements StreamRepository, SyncableRepository,
    ExternalStreamRepository {

    private final StreamEntityMapper streamEntityMapper;
    private final StreamDataSource localStreamDataSource;
    private final StreamDataSource remoteStreamDataSource;
    private final SyncableStreamEntityFactory syncableStreamEntityFactory;
    private final StreamCache streamCache;

    @Inject
    public SyncStreamRepository(StreamEntityMapper streamEntityMapper, @Local StreamDataSource localStreamDataSource,
      @Remote StreamDataSource remoteStreamDataSource, SyncableStreamEntityFactory syncableStreamEntityFactory,
      StreamCache streamCache) {
        this.localStreamDataSource = localStreamDataSource;
        this.remoteStreamDataSource = remoteStreamDataSource;
        this.streamEntityMapper = streamEntityMapper;
        this.syncableStreamEntityFactory = syncableStreamEntityFactory;
        this.streamCache = streamCache;
    }

    @Override public Stream getStreamById(String idStream, String[] types) {
        StreamEntity streamEntity = remoteStreamDataSource.getStreamById(idStream, types);
        if (streamEntity != null) {
            markEntityAsSynchronized(streamEntity);
            localStreamDataSource.putStream(streamEntity);
            Stream stream = streamEntityMapper.transform(streamEntity);
            streamCache.putStream(stream);
            return stream;
        } else {
            return null;
        }
    }

    @Override public List<Stream> getStreamsByIds(List<String> streamIds, String[] types) {
        List<StreamEntity> remoteEvents = remoteStreamDataSource.getStreamByIds(streamIds, types);
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

        StreamEntity remoteStreamEntity = remoteStreamDataSource.createStream(currentOrNewEntity);
        markEntityAsSynchronized(remoteStreamEntity);
        localStreamDataSource.putStream(remoteStreamEntity);
        return streamEntityMapper.transform(remoteStreamEntity);
    }

    @Override public Stream updateStream(StreamUpdateParameters streamUpdateParameters) {
        StreamEntity streamEntity = remoteStreamDataSource.updateStream(streamUpdateParameters);
        localStreamDataSource.putStream(streamEntity);
        return streamEntityMapper.transform(streamEntity);
    }

    @Override public void shareStream(String idStream) {
        remoteStreamDataSource.shareStream(idStream);
    }

    @Override public void removeStream(String idStream) {
        remoteStreamDataSource.removeStream(idStream);
    }

    @Override public void restoreStream(String idStream) {
        remoteStreamDataSource.restoreStream(idStream);
    }

    @Override public String getLastTimeFiltered(String idStream) {
        return "0";
    }

    @Override public void putLastTimeFiltered(String idStream, String lastTimeFiltered) {
        throw new RuntimeException("Method not implemented yet!");
    }

    @Override public Stream getBlogStream(String country, String language) {
        StreamEntity blogStream = remoteStreamDataSource.getBlogStream(country, language);
        if (blogStream != null) {
            markEntityAsSynchronized(blogStream);
            localStreamDataSource.putStream(blogStream);
            return streamEntityMapper.transform(blogStream);
        } else {
            return null;
        }
    }

    @Override public Stream getHelpStream(String country, String language) {
        StreamEntity helpStream = remoteStreamDataSource.getHelpStream(country, language);
        if (helpStream != null) {
            markEntityAsSynchronized(helpStream);
            localStreamDataSource.putStream(helpStream);
            return streamEntityMapper.transform(helpStream);
        } else {
            return null;
        }
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
