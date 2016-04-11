package com.shootr.mobile.data.repository.datasource.event;

import com.shootr.mobile.data.entity.StreamEntity;
import java.util.List;

public interface StreamListDataSource {

    List<StreamEntity> getStreamList(String locale);

    List<StreamEntity> getStreams(String query, String locale);
}
