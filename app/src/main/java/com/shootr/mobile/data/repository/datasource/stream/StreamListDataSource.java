package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.StreamEntity;
import java.util.List;

public interface StreamListDataSource {

    List<StreamEntity> getStreamList(String locale, String[] types);

    List<StreamEntity> getStreams(String query, String locale, String[] types);
}
