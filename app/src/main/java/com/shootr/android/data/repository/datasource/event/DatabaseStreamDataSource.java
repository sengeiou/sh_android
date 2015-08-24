package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.data.entity.WatchersEntity;
import com.shootr.android.db.manager.StreamManager;
import com.shootr.android.domain.exception.DeleteStreamNotAllowedException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseStreamDataSource implements StreamDataSource {

    private final StreamManager streamManager;

    @Inject public DatabaseStreamDataSource(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override public StreamEntity getStreamById(String idStream) {
        return streamManager.getStreamById(idStream);
    }

    @Override public List<StreamEntity> getStreamByIds(List<String> streamIds) {
        return streamManager.getStreamsByIds(streamIds);
    }

    @Override public StreamEntity putStream(StreamEntity streamEntity) {
        streamManager.saveStream(streamEntity);
        return streamEntity;
    }

    @Override public List<StreamEntity> putStreams(List<StreamEntity> streams) {
        streamManager.saveStreams(streams);
        return streams;
    }

    @Override public Integer getListingCount(String idUser) {
        return streamManager.getListingCount(idUser);
    }

    @Override public List<StreamEntity> getStreamsListing(String idUser) {
        return streamManager.getStreamsListing(idUser);
    }

    @Override
    public void deleteStream(String idStream) throws DeleteStreamNotAllowedException {
        streamManager.deleteStream(idStream);
    }

    @Override public Integer getWatchersForStream(String idStream) {
        throw new IllegalArgumentException("method not implemented in local datasource");
    }

    @Override public List<WatchersEntity> getWatchers() {
        throw new IllegalArgumentException("method not implemented in local datasource");
    }
}
