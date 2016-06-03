package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.db.manager.StreamManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseStreamDataSource implements StreamDataSource {

    private final StreamManager streamManager;

    @Inject public DatabaseStreamDataSource(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override public StreamEntity getStreamById(String idStream, String[] types) {
        return streamManager.getStreamById(idStream);
    }

    @Override public List<StreamEntity> getStreamByIds(List<String> streamIds, String[] types) {
        return streamManager.getStreamsByIds(streamIds);
    }

    @Override public StreamEntity putStream(StreamEntity streamEntity, Boolean notifyMessage) {
        streamManager.saveStream(streamEntity);
        return streamEntity;
    }

    @Override public StreamEntity putStream(StreamEntity streamEntity) {
        return putStream(streamEntity, false);
    }

    @Override public List<StreamEntity> putStreams(List<StreamEntity> streams) {
        streamManager.saveStreams(streams);
        return streams;
    }

    @Override public List<StreamEntity> getStreamsListing(String idUser, String[] types) {
        return streamManager.getStreamsListingNotRemoved(idUser);
    }

    @Override public void shareStream(String idStream) {
        throw new IllegalStateException("Not allowed in local");
    }

    @Override public void removeStream(String idStream) {
        streamManager.removeStream(idStream);
    }

    @Override public void restoreStream(String idStream) {
        streamManager.restoreStream(idStream);
    }

    @Override public StreamEntity getBlogStream(String country, String language) {
        throw new IllegalArgumentException("method not implemented in local datasource");
    }

    @Override public StreamEntity getHelpStream(String country, String language) {
        throw new IllegalArgumentException("method not implemented in local datasource");
    }
}
