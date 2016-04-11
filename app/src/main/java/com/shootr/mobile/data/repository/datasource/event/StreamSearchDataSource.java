package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.StreamSearchEntity;
import java.util.List;

public interface StreamSearchDataSource {

    List<StreamSearchEntity> getDefaultStreams(String locale);

    void putDefaultStreams(List<StreamSearchEntity> transform);

    void deleteDefaultStreams();

    StreamSearchEntity getStreamResult(String idStream);
}
