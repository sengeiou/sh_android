package com.shootr.mobile.ui.model;

import com.shootr.mobile.domain.model.ExternalVideo;
import javax.inject.Inject;

public class ExternalVideoModelMapper {

  @Inject public ExternalVideoModelMapper() {
  }

  public ExternalVideoModel map(ExternalVideo externalVideo) {
    ExternalVideoModel model = new ExternalVideoModel();

    model.setProvider(externalVideo.getProvider());
    model.setVideoId(externalVideo.getVideoId());

    return model;
  }
}
