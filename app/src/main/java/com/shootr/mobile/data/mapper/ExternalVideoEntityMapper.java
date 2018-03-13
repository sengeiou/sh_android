package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.ExternalVideoEntity;
import com.shootr.mobile.domain.model.ExternalVideo;
import javax.inject.Inject;

public class ExternalVideoEntityMapper {

  @Inject public ExternalVideoEntityMapper() {
  }

  public ExternalVideo transform(ExternalVideoEntity entity) {
    ExternalVideo externalVideo = new ExternalVideo();

    externalVideo.setProvider(entity.getProvider());
    externalVideo.setVideoId(entity.getVideoId());

    return externalVideo;
  }
}
