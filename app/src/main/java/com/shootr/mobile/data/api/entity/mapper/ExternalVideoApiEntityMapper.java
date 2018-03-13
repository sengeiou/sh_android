package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ExternalVideoApiEntity;
import com.shootr.mobile.data.entity.ExternalVideoEntity;


public class ExternalVideoApiEntityMapper {

  public ExternalVideoEntity transform(ExternalVideoApiEntity apiEntity) {

    ExternalVideoEntity externalVideoEntity = new ExternalVideoEntity();

    externalVideoEntity.setProvider(apiEntity.getProvider());
    externalVideoEntity.setVideoId(apiEntity.getVideoId());

    return externalVideoEntity;
  }
}
