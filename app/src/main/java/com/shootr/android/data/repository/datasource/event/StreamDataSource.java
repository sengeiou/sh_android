package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.EventEntity;
import com.shootr.android.domain.exception.DeleteEventNotAllowedException;
import java.util.List;

public interface StreamDataSource {

    EventEntity getStreamById(String idStream);

    List<EventEntity> getStreamByIds(List<String> streamIds);

    EventEntity putStream(EventEntity streamEntity);

    List<EventEntity> putStreams(List<EventEntity> streams);

    Integer getListingCount(String idUser);

    List<EventEntity> getStreamsListing(String idUser);

    void deleteStream(String idStream) throws DeleteEventNotAllowedException;
}
