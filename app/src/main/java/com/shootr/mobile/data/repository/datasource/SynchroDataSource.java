package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.entity.SynchroEntity;

public interface SynchroDataSource {

  String FOLLOW = "follow";
  String USER = "user";

  void putEntity(SynchroEntity synchroEntity);

  Long getTimestamp(String entity);

}
