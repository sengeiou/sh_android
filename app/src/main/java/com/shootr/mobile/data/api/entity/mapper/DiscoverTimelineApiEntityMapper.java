package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.DiscoverStreamApiEntity;
import com.shootr.mobile.data.api.entity.DiscoverTimelineApiEntity;
import com.shootr.mobile.data.entity.DiscoverStreamEntity;
import com.shootr.mobile.data.entity.DiscoverTimelineEntity;
import java.util.ArrayList;
import javax.inject.Inject;

public class DiscoverTimelineApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;

  @Inject public DiscoverTimelineApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
  }

  public DiscoverTimelineEntity map(DiscoverTimelineApiEntity discoverTimelineApiEntity) {
    DiscoverTimelineEntity discoverTimelineEntity = new DiscoverTimelineEntity();

    if (discoverTimelineApiEntity.getDiscoverStreamApiEntities() != null) {

      ArrayList<DiscoverStreamEntity> discoverStreamEntities = new ArrayList<>();

      for (DiscoverStreamApiEntity discoverStreamApiEntity : discoverTimelineApiEntity.getDiscoverStreamApiEntities()) {
        DiscoverStreamEntity discoverStreamEntity = new DiscoverStreamEntity();
        discoverStreamEntity.setStreamEntity(discoverStreamApiEntity.getStreamEntity());
        discoverStreamEntity.setShotEntities(new ArrayList<>(
            shotApiEntityMapper.transform(discoverStreamApiEntity.getShotApiEntities())));
        discoverStreamEntities.add(discoverStreamEntity);
      }

      discoverTimelineEntity.setDiscoverStreamEntities(discoverStreamEntities);
    }
    return discoverTimelineEntity;
  }
}
