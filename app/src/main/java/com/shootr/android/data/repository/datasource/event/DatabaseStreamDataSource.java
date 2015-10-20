package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamEntity;
import com.shootr.android.db.manager.StreamManager;
import java.util.List;
import java.util.Map;
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

    @Override public List<StreamEntity> getStreamsListing(String idUser) {
        return streamManager.getStreamsListingNotRemoved(idUser);
    }

    @Override public void shareStream(String idStream) {
        throw new IllegalStateException("Not allowed in local");
    }

    @Override public Map<String, Integer> getHolderFavorites() {
        throw new IllegalArgumentException("method not implemented in local datasource");
    }

    @Override public void removeStream(String idStream) {
        streamManager.removeStream(idStream);
    }

    @Override public void restoreStream(String idStream) {
        streamManager.restoreStream(idStream);
    }
}
