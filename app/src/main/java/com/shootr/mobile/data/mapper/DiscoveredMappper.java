package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.DiscoveredEntity;
import com.shootr.mobile.domain.model.discover.Discovered;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DiscoveredMappper {

  private final StreamEntityMapper streamEntityMapper;

  @Inject public DiscoveredMappper(StreamEntityMapper streamEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
  }

  public Discovered map(DiscoveredEntity value) {
    Discovered discovered = new Discovered();
    discovered.setIdDiscover(value.getIdDiscover());
    discovered.setRelevance(value.getRelevance());
    discovered.setStream(streamEntityMapper.transform(value.getStream()));
    discovered.setType(value.getType());
    return discovered;
  }

  public List<Discovered> map(List<DiscoveredEntity> values) {
    List<Discovered> returnValues = new ArrayList<>(values.size());
    for (DiscoveredEntity value : values) {
      returnValues.add(map(value));
    }
    return returnValues;
  }
}