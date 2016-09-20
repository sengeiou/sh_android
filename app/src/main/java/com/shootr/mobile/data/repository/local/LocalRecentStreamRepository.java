package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.data.entity.RecentStreamEntity;
import com.shootr.mobile.data.mapper.RecentStreamEntityMapper;
import com.shootr.mobile.data.mapper.StreamEntityMapper;
import com.shootr.mobile.data.repository.datasource.stream.RecentStreamDataSource;
import com.shootr.mobile.domain.model.stream.Stream;
import com.shootr.mobile.domain.model.stream.StreamSearchResult;
import com.shootr.mobile.domain.repository.RecentStreamRepository;
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
    localStreamDataSource.putRecentStream(streamEntityMapper.transform(stream), currentTime);
  }

  @Override public void removeRecentStream(String idStream) {
    localStreamDataSource.removeRecentStream(idStream);
  }

  @Override public List<StreamSearchResult> getDefaultStreams() {
    List<RecentStreamEntity> recentStreamEntities = localStreamDataSource.getRecentStreams();
    return recentStreamEntityMapper.transform(recentStreamEntities);
  }
}
