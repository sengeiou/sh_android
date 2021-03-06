package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ExternalVideoApiEntity;
import com.shootr.mobile.data.entity.ExternalVideoEntity;
import javax.inject.Inject;

public class ExternalVideoApiEntityMapper {

  @Inject public ExternalVideoApiEntityMapper() {
  }

  public ExternalVideoEntity transform(ExternalVideoApiEntity apiEntity) {

    ExternalVideoEntity externalVideoEntity = new ExternalVideoEntity();

    externalVideoEntity.setProvider(apiEntity.getProvider());
    externalVideoEntity.setVideoId(apiEntity.getVideoId());
    externalVideoEntity.setDeleted(apiEntity.getDeleted());
    externalVideoEntity.setIdExternalVideo(apiEntity.getIdExternalVideo());

    return externalVideoEntity;
  }
}
