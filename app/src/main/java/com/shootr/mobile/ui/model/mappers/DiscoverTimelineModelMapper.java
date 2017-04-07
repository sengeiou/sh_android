package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.discover.DiscoverStream;
import com.shootr.mobile.domain.model.discover.DiscoverTimeline;
import com.shootr.mobile.ui.model.DiscoverStreamModel;
import com.shootr.mobile.ui.model.DiscoverTimelineModel;
import java.util.ArrayList;
import javax.inject.Inject;

public class DiscoverTimelineModelMapper {

  private final StreamModelMapper mapper;
  private final ShotModelMapper shotModelMapper;

  @Inject public DiscoverTimelineModelMapper(StreamModelMapper mapper, ShotModelMapper shotModelMapper) {
    this.mapper = mapper;
    this.shotModelMapper = shotModelMapper;
  }

  public DiscoverTimelineModel map(DiscoverTimeline discoverTimeline) {
    DiscoverTimelineModel discoverTimelineModel = new DiscoverTimelineModel();

    if (discoverTimeline.getDiscoverStreams() != null) {
      ArrayList<DiscoverStreamModel> discoverStreams = new ArrayList<>();

      for (DiscoverStream discoverStream : discoverTimeline.getDiscoverStreams()) {
        DiscoverStreamModel discoverStreamModel = new DiscoverStreamModel();
        discoverStreamModel.setStreamModel(mapper.transform(discoverStream.getStream()));
        discoverStreamModel.setShotModels(new ArrayList<>(shotModelMapper.transform(discoverStream.getShots())));
        discoverStreamModel.getStreamModel().setFavorite(discoverStream.isFavorite());

        discoverStreams.add(discoverStreamModel);
      }
      discoverTimelineModel.setDiscoverStreamModels(discoverStreams);
    }

    return discoverTimelineModel;
  }
}
