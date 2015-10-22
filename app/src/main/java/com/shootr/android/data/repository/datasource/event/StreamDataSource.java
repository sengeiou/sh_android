package com.shootr.android.data.repository.datasource.event;

import com.shootr.android.data.entity.StreamEntity;
import java.util.List;
import java.util.Map;

public interface StreamDataSource {

    StreamEntity getStreamById(String idStream);

    List<StreamEntity> getStreamByIds(List<String> streamIds);

    StreamEntity putStream(StreamEntity streamEntity);

    List<StreamEntity> putStreams(List<StreamEntity> streams);

    List<StreamEntity> getStreamsListing(String idUser);

    void shareStream(String idStream);

    Map<String, Integer> getHolderFavorites();

    void removeStream(String idStream);

    void restoreStream(String idStream);

    StreamEntity getBlogStream(String country, String language);

    StreamEntity getHelpStream(String country, String language);
}
