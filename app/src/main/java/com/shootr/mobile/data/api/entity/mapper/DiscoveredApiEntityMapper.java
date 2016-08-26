package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.DiscoveredApiEntity;
import com.shootr.mobile.data.entity.DiscoveredEntity;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DiscoveredApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;

  @Inject public DiscoveredApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
  }

  public DiscoveredEntity map(DiscoveredApiEntity value) {
    DiscoveredEntity discoveredEntity = new DiscoveredEntity();
    discoveredEntity.setIdDiscover(value.getIdDiscover());
    discoveredEntity.setRelevance(value.getRelevance());
    discoveredEntity.setStream(value.getStream());
    discoveredEntity.setType(value.getType());
    discoveredEntity.setShot(shotApiEntityMapper.transform(value.getShot()));
    return discoveredEntity;
  }

  public List<DiscoveredEntity> map(List<DiscoveredApiEntity> values) {
    List<DiscoveredEntity> returnValues = new ArrayList<>(values.size());
    for (DiscoveredApiEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}
