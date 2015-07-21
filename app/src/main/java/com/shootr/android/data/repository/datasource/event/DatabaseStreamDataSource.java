package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.manager.StreamManager;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseStreamDataSource implements StreamDataSource {

    private final StreamManager streamManager;

    @Inject public DatabaseStreamDataSource(StreamManager streamManager) {
        this.streamManager = streamManager;
    }

    @Override public EventEntity getStreamById(String idStream) {
        return streamManager.getStreamById(idStream);
    }

    @Override public List<EventEntity> getStreamByIds(List<String> streamIds) {
        return streamManager.getStreamsByIds(streamIds);
    }

    @Override public EventEntity putStream(EventEntity streamEntity) {
        streamManager.saveStream(streamEntity);
        return streamEntity;
    }

    @Override public List<EventEntity> putStreams(List<EventEntity> streams) {
        streamManager.saveStreams(streams);
        return streams;
    }

    @Override public Integer getListingCount(String idUser) {
        return streamManager.getListingCount(idUser);
    }

    @Override public List<EventEntity> getStreamsListing(String idUser) {
        return streamManager.getStreamsListing(idUser);
    }

    @Override
    public void deleteStream(String idStream) throws DeleteEventNotAllowedException {
        streamManager.deleteStream(idStream);
    }
}
