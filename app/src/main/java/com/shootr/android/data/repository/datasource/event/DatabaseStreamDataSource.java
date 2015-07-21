package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.db.manager.EventManager;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import java.util.List;
import javax.inject.Inject;

public class DatabaseStreamDataSource implements StreamDataSource {

    private final EventManager eventManager;

    @Inject public DatabaseStreamDataSource(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override public EventEntity getStreamById(String idStream) {
        return eventManager.getEventById(idStream);
    }

    @Override public List<EventEntity> getStreamByIds(List<String> streamIds) {
        return eventManager.getEventsByIds(streamIds);
    }

    @Override public EventEntity putStream(EventEntity streamEntity) {
        eventManager.saveEvent(streamEntity);
        return streamEntity;
    }

    @Override public List<EventEntity> putStreams(List<EventEntity> streams) {
        eventManager.saveEvents(streams);
        return streams;
    }

    @Override public Integer getListingCount(String idUser) {
        return eventManager.getListingCount(idUser);
    }

    @Override public List<EventEntity> getStreamsListing(String idUser) {
        return eventManager.getEventsListing(idUser);
    }

    @Override
    public void deleteStream(String idStream) throws DeleteEventNotAllowedException {
        eventManager.deleteEvent(idStream);
    }
}
