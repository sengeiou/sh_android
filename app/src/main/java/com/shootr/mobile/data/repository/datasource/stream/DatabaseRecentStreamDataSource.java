package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.db.manager.RecentSearchManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseRecentStreamDataSource implements RecentStreamDataSource {

  private final RecentSearchManager recentSearchManager;

  @Inject public DatabaseRecentStreamDataSource(RecentSearchManager recentSearchManager) {
    this.recentSearchManager = recentSearchManager;
  }

  @Override public void putRecentStream(StreamEntity streamEntity, long currentTime) {
    RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
    recentSearchEntity.setStream(streamEntity);
    recentSearchEntity.setVisitDate(currentTime);
    recentSearchManager.saveRecentSearch(recentSearchEntity);
  }

  @Override public List<RecentSearchEntity> getRecentStreams() {
    return recentSearchManager.readRecentSearches();
  }
}
