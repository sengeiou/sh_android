package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.LandingStreamsEntity;
import com.shootr.mobile.domain.model.HotStreams;
import com.shootr.mobile.domain.model.UserStreams;
import com.shootr.mobile.domain.model.stream.LandingStreams;
import java.util.ArrayList;
import javax.inject.Inject;

public class LandingStreamsEntityMapper {

  private final StreamEntityMapper streamEntityMapper;

  @Inject public LandingStreamsEntityMapper(StreamEntityMapper streamEntityMapper) {
    this.streamEntityMapper = streamEntityMapper;
  }

  public LandingStreams transform(LandingStreamsEntity landingStreamsEntity) {

    LandingStreams landingStreams = new LandingStreams();

    HotStreams hotStreams = new HotStreams();
    UserStreams userStreams = new UserStreams();

    hotStreams.setStreams(
        new ArrayList<>(streamEntityMapper.transform(landingStreamsEntity.getHot().getData())));
    userStreams.setStreams(new ArrayList<>(
        streamEntityMapper.transform(landingStreamsEntity.getUserStreams().getData())));

    landingStreams.setUserStreams(userStreams);
    landingStreams.setHotStreams(hotStreams);

    return landingStreams;
  }
}
