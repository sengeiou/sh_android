package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.RecentSearchEntity;
import com.shootr.mobile.data.mapper.RecentStreamEntityMapper;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.RecentStreamDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.stream.RecentStreamRepository;
import java.util.List;
import javax.inject.Inject;

public class LocalRecentStreamRepository implements RecentStreamRepository {

  private final RecentStreamDataSource localStreamDataSource;
  private final RecentStreamEntityMapper recentStreamEntityMapper;
  private final StreamEntityMapper streamEntityMapper;

  @Inject public LocalRecentStreamRepository(RecentStreamDataSource localStreamDataSource,
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

  @Override public List<StreamSearchResult> getDefaultStreams() {
    List<RecentSearchEntity> recentStreamEntities = localStreamDataSource.getRecentStreams();
    return recentStreamEntityMapper.transform(recentStreamEntities);
  }
}
