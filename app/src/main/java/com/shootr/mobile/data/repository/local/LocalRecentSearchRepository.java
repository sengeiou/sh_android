package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.mapper.RecentStreamEntityMapper;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.RecentSearchDataSource;
import com.shootr.mobile.domain.model.Searchable;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.user.User;
import com.shootr.mobile.domain.repository.stream.RecentSearchRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalRecentSearchRepository implements RecentSearchRepository {

  private final RecentSearchDataSource localStreamDataSource;
  private final RecentStreamEntityMapper recentStreamEntityMapper;
  private final StreamEntityMapper streamEntityMapper;

  @Inject public LocalRecentSearchRepository(RecentSearchDataSource localStreamDataSource,
      RecentStreamEntityMapper recentStreamEntityMapper, StreamEntityMapper streamEntityMapper) {
    this.localStreamDataSource = localStreamDataSource;
    this.recentStreamEntityMapper = recentStreamEntityMapper;
    this.streamEntityMapper = streamEntityMapper;
  }

  @Override public void putRecentStream(Stream stream, long currentTime) {
    if (stream != null) {
      localStreamDataSource.putRecentStream(streamEntityMapper.transform(stream), currentTime);
    }
  }

  @Override public void putRecentUser(User user, long currentTime) {

  }

  @Override public List<Searchable> getDefaultSearch() {
    List<RecentSearchEntity> recentStreamEntities = localStreamDataSource.getRecentSearches();
    return recentStreamEntityMapper.transform(recentStreamEntities);
  }
}
