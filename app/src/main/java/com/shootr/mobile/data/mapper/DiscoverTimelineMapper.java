package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.DiscoverStreamEntity;
import com.shootr.mobile.data.entity.DiscoverTimelineEntity;
import com.shootr.mobile.domain.model.discover.DiscoverStream;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import java.util.ArrayList;
import javax.inject.Inject;

public class DiscoverTimelineMapper {

  private final StreamEntityMapper streamEntityMapper;
  private final ShotEntityMapper shotEntityMapper;

  @Inject public DiscoverTimelineMapper(StreamEntityMapper streamEntityMapper,
      ShotEntityMapper shotEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
    this.shotEntityMapper = shotEntityMapper;
  }

  public DiscoverTimeline map(DiscoverTimelineEntity entity) {
    DiscoverTimeline discoverTimeline = new DiscoverTimeline();

    if (entity.getDiscoverStreamEntities() != null) {
      ArrayList<DiscoverStream> discoverStreams = new ArrayList<>();

      for (DiscoverStreamEntity discoverStreamEntity : entity.getDiscoverStreamEntities()) {
        DiscoverStream discoverStream = new DiscoverStream();
        discoverStream.setStream(streamEntityMapper.transform(discoverStreamEntity.getStreamEntity()));
        discoverStream.setShots(new ArrayList<>(shotEntityMapper.transform(discoverStreamEntity.getShotEntities())));

        discoverStreams.add(discoverStream);
      }
      discoverTimeline.setDiscoverStreams(discoverStreams);
    }

    return discoverTimeline;
  }
}
