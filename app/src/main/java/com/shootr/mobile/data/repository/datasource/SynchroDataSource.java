package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.entity.SynchroEntity;

public interface SynchroDataSource {

  String FOLLOW = "follow";
  String USER = "user";
  String PRIVATE_MESSAGE_CHANNEL = "privateMessageChannel";

  void putEntity(SynchroEntity synchroEntity);

  Long getTimestamp(String entity);

}
