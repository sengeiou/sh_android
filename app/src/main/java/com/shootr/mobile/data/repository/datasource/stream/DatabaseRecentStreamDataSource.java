package com.shootr.mobile.data.repository.datasource.stream;

import com.shootr.mobile.data.entity.RecentStreamEntity;
import com.shootr.mobile.data.entity.StreamEntity;
import com.shootr.mobile.db.manager.RecentStreamManager;
import java.util.List;
import javax.inject.Inject;

public class DatabaseRecentStreamDataSource implements RecentStreamDataSource {

  private final RecentStreamManager recentStreamManager;

  @Inject public DatabaseRecentStreamDataSource(RecentStreamManager recentStreamManager) {
    this.recentStreamManager = recentStreamManager;
  }

  @Override public void putRecentStream(StreamEntity streamEntity, long currentTime) {
    RecentStreamEntity recentStreamEntity = new RecentStreamEntity();
    recentStreamEntity.setStream(streamEntity);
    recentStreamEntity.setJoinStreamDate(currentTime);
    recentStreamManager.saveStream(recentStreamEntity);
  }

  @Override public List<RecentStreamEntity> getRecentStreams() {
    return recentStreamManager.readRecentStreams();
  }
}
