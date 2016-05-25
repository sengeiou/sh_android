package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.StreamEntity;
import java.util.List;

public interface StreamDataSource {

    StreamEntity getStreamById(String idStream, String[] types);

    List<StreamEntity> getStreamByIds(List<String> streamIds, String[] types);

    StreamEntity putStream(StreamEntity streamEntity, Boolean notifyStreamMessage);

    StreamEntity putStream(StreamEntity streamEntity);

    List<StreamEntity> putStreams(List<StreamEntity> streams);

    List<StreamEntity> getStreamsListing(String idUser, String[] types);

    void shareStream(String idStream);

    void removeStream(String idStream);

    void restoreStream(String idStream);

    StreamEntity getBlogStream(String country, String language);

    StreamEntity getHelpStream(String country, String language);
}
