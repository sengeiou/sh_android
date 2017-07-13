package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.api.entity.FollowsEntity;
import com.shootr.mobile.data.entity.FollowableEntity;
import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.data.entity.UserEntity;
import com.shootr.mobile.data.repository.datasource.user.DatabaseUserDataSource;
import com.shootr.mobile.db.manager.RecentSearchManager;
import com.shootr.mobile.domain.model.SearchableType;
import java.util.List;
import javax.inject.Inject;

public class DatabaseRecentSearchDataSource implements RecentSearchDataSource {

  private final RecentSearchManager recentSearchManager;
  private final DatabaseStreamDataSource databaseStreamDataSource;
  private final DatabaseUserDataSource databaseUserDataSource;

  @Inject public DatabaseRecentSearchDataSource(RecentSearchManager recentSearchManager,
      DatabaseStreamDataSource databaseStreamDataSource,
      DatabaseUserDataSource databaseUserDataSource) {
    this.recentSearchManager = recentSearchManager;
    this.databaseStreamDataSource = databaseStreamDataSource;
    this.databaseUserDataSource = databaseUserDataSource;
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

  @Override public void putRecentSearchItems(FollowsEntity followsEntity) {
    int position = 0;
    for (FollowableEntity followableEntity : followsEntity.getData()) {
      switch (followableEntity.getResultType()) {
        case SearchableType.STREAM:
          putRecentStream((StreamEntity) followableEntity, position);
          databaseStreamDataSource.putStream((StreamEntity) followableEntity);
          break;
        case SearchableType.USER:
          putRecentUser((UserEntity) followableEntity, position);
          databaseUserDataSource.putUser((UserEntity) followableEntity);
          break;
        default:
          break;
      }
      position++;
    }
  }

  @Override public boolean isRecentSearchEmpty() {
    return recentSearchManager.isRecentSearchEmpty();
  }
}
