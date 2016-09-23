package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.entity.SynchroEntity;
import com.shootr.mobile.db.manager.SynchroManager;
import javax.inject.Inject;

public class LocalSynchroDataSource implements SynchroDataSource {

  private final SynchroManager synchroManager;

  @Inject public LocalSynchroDataSource(SynchroManager synchroManager) {
    this.synchroManager = synchroManager;
  }

  @Override public void putEntity(SynchroEntity synchroEntity) {
    synchroManager.putEntity(synchroEntity);
  }

  @Override public Long getTimestamp(String entity) {
    return synchroManager.getTimestamp(entity);
  }
}
