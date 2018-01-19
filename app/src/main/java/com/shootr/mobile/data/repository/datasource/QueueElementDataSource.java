package com.shootr.mobile.data.repository.datasource;

public interface QueueElementDataSource {

  void sendQueue();

  void sendBackToQueue(String idStream, String queueElementType);
}
