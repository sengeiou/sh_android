package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.discover.Discovered;
import com.shootr.mobile.domain.model.discover.DiscoveredType;
import com.shootr.mobile.ui.model.DiscoveredModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DiscoveredModelMapper {

  private StreamModelMapper mapper;
  private ShotModelMapper shotModelMapper;

  @Inject public DiscoveredModelMapper(StreamModelMapper mapper, ShotModelMapper shotModelMapper) {
    this.mapper = mapper;
    this.shotModelMapper = shotModelMapper;
  }

  public DiscoveredModel transform(Discovered discovered) {
    if (discovered == null) {
      return null;
    }

    DiscoveredModel discoveredModel = new DiscoveredModel();
    discoveredModel.setIdDiscover(discovered.getIdDiscover());
    discoveredModel.setRelevance(discovered.getRelevance());
    discoveredModel.setType(discovered.getType());
    discoveredModel.setHasBeenFaved(discovered.isFaved());
    if (discoveredModel.getType().equals(DiscoveredType.STREAM)) {
      discoveredModel.setStreamModel(mapper.transform(discovered.getStream()));
    } else {
      discoveredModel.setShotModel(shotModelMapper.transform(discovered.getShot()));
    }

    return discoveredModel;
  }

  public List<DiscoveredModel> transform(List<Discovered> discovereds) {
    ArrayList<DiscoveredModel> discoveredModels = new ArrayList<>();
    for (Discovered discovered : discovereds) {
      discoveredModels.add(transform(discovered));
    }
    return discoveredModels;
  }
}
