package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.RecentStreamEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import java.util.List;

public interface RecentStreamDataSource {

    void putRecentStream(StreamEntity stream, long currentTime);

    void removeRecentStream(String idStream);

    List<RecentStreamEntity> getRecentStreams();
}
