package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamEntity;
import java.util.List;

public interface StreamDataSource {

    StreamEntity getStreamById(String idStream);

    List<StreamEntity> getStreamByIds(List<String> streamIds);

    StreamEntity putStream(StreamEntity streamEntity);

    List<StreamEntity> putStreams(List<StreamEntity> streams);

    Integer getListingCount(String idUser);

    List<StreamEntity> getStreamsListing(String idUser);
}
