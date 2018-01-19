package com.shootr.mobile.data.repository.local;

import com.shootr.mobile.domain.repository.QueueRepository;
import javax.inject.Inject;

public class LocalQueueRepository implements QueueRepository {

  @Inject public LocalQueueRepository() {
  }

  @Override public void sendShootQueue() {
    throw new IllegalStateException("Method not valid for local repository");
  }

  @Override public void getShootrQueue() {
    throw new IllegalStateException("Method not valid for local repository");
  }
}
