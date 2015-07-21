package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.data.mapper.EventEntityMapper;
import com.shootr.android.data.repository.datasource.event.StreamDataSource;
import com.shootr.android.data.repository.datasource.event.EventSearchDataSource;
import com.shootr.android.domain.Event;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import com.shootr.android.domain.repository.StreamRepository;
import com.shootr.android.domain.repository.Local;
import java.util.List;
import javax.inject.Inject;

public class LocalStreamRepository implements StreamRepository {

    private final StreamDataSource localStreamDataSource;
    private final EventSearchDataSource localEventSearchDataSource;
    private final EventEntityMapper eventEntityMapper;

    @Inject public LocalStreamRepository(@Local StreamDataSource localStreamDataSource,
      @Local EventSearchDataSource localEventSearchDataSource, EventEntityMapper eventEntityMapper) {
        this.localStreamDataSource = localStreamDataSource;
        this.localEventSearchDataSource = localEventSearchDataSource;
        this.eventEntityMapper = eventEntityMapper;
    }

    @Override public Event getStreamById(String idStream) {
        EventEntity eventEntity = localStreamDataSource.getStreamById(idStream);
        if (eventEntity == null) {
            eventEntity = fallbackOnSearchResults(idStream);
        }
        return eventEntityMapper.transform(eventEntity);
    }

    private EventEntity fallbackOnSearchResults(String idEvent) {
        EventEntity eventEntity = localEventSearchDataSource.getEventResult(idEvent);
        if (eventEntity != null) {
            localStreamDataSource.putStream(eventEntity);
        }
        return eventEntity;
    }

    @Override public List<Event> getStreamsByIds(List<String> streamIds) {
        List<EventEntity> eventEntities = localStreamDataSource.getStreamByIds(streamIds);
        return eventEntityMapper.transform(eventEntities);
    }

    @Override public Event putStream(Event event) {
        EventEntity eventEntity = eventEntityMapper.transform(event);
        localStreamDataSource.putStream(eventEntity);
        return event;
    }

    @Override public Event putStream(Event event, boolean notify) {
        throw new IllegalStateException("Notify not allowed in local repository.");
    }

    @Override public Integer getListingCount(String idUser) {
        return localStreamDataSource.getListingCount(idUser);
    }

    @Override
    public void deleteStream(String idEvent) throws DeleteEventNotAllowedException {
        localStreamDataSource.deleteStream(idEvent);
    }
}
