package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.Discovered;
import com.shootr.mobile.ui.model.DiscoveredModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class DiscoveredModelMapper {

  private StreamModelMapper mapper;

  @Inject public DiscoveredModelMapper(StreamModelMapper mapper) {
    this.mapper = mapper;
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
    discoveredModel.setStreamModel(mapper.transform(discovered.getStream()));

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
