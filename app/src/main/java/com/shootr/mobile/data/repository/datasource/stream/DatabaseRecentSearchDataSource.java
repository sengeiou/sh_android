package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.db.manager.RecentSearchManager;
import com.shootr.mobile.domain.model.SearchableType;
import java.util.List;
import javax.inject.Inject;

public class DatabaseRecentSearchDataSource implements RecentSearchDataSource {

  private final RecentSearchManager recentSearchManager;

  @Inject public DatabaseRecentSearchDataSource(RecentSearchManager recentSearchManager) {
    this.recentSearchManager = recentSearchManager;
  }

  @Override public void putRecentStream(StreamEntity streamEntity, long currentTime) {
    RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
    recentSearchEntity.setStream(streamEntity);
    recentSearchEntity.setVisitDate(currentTime);
    recentSearchEntity.setSearchableType(SearchableType.STREAM);
    recentSearchManager.saveRecentSearch(recentSearchEntity);
  }

  @Override public void putRecentUser(UserEntity userEntity, long currentTime) {
    RecentSearchEntity recentSearchEntity = new RecentSearchEntity();
    recentSearchEntity.setUser(userEntity);
    recentSearchEntity.setVisitDate(currentTime);
    recentSearchEntity.setSearchableType(SearchableType.USER);
    recentSearchManager.saveRecentSearch(recentSearchEntity);
  }

  @Override public List<RecentSearchEntity> getRecentSearches() {
    return recentSearchManager.readRecentSearches();
  }
}