package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.repository.datasource.ServiceQueueElementDatasource;
import com.shootr.mobile.domain.repository.QueueElementRepository;
import javax.inject.Inject;

public class RemoteQueueElementRepository implements QueueElementRepository {

  private final ServiceQueueElementDatasource serviceQueueElementDatasource;

  @Inject public RemoteQueueElementRepository(
      ServiceQueueElementDatasource serviceQueueElementDatasource) {
    this.serviceQueueElementDatasource = serviceQueueElementDatasource;
  }

  @Override public void sendQueue() {
    serviceQueueElementDatasource.sendQueue();
  }
}
