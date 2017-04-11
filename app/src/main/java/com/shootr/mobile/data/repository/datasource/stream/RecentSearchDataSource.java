package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import java.util.List;

public interface RecentSearchDataSource {

    void putRecentStream(StreamEntity stream, long currentTime);

    List<RecentSearchEntity> getRecentSearches();
}
