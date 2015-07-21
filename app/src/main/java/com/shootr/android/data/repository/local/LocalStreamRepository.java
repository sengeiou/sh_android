package com.shootr.android.data.repository.local;

import com.shootr.android.data.entity.StreamEntity;
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
        StreamEntity streamEntity = localStreamDataSource.getStreamById(idStream);
        if (streamEntity == null) {
            streamEntity = fallbackOnSearchResults(idStream);
        }
        return eventEntityMapper.transform(streamEntity);
    }

    private StreamEntity fallbackOnSearchResults(String idEvent) {
        StreamEntity streamEntity = localEventSearchDataSource.getEventResult(idEvent);
        if (streamEntity != null) {
            localStreamDataSource.putStream(streamEntity);
        }
        return streamEntity;
    }

    @Override public List<Event> getStreamsByIds(List<String> streamIds) {
        List<StreamEntity> eventEntities = localStreamDataSource.getStreamByIds(streamIds);
        return eventEntityMapper.transform(eventEntities);
    }

    @Override public Event putStream(Event event) {
        StreamEntity streamEntity = eventEntityMapper.transform(event);
        localStreamDataSource.putStream(streamEntity);
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
