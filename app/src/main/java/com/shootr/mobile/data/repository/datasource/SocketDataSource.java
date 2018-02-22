package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SocketMessageEntity;
import io.reactivex.Observable;

public interface SocketDataSource {

  Observable<SocketMessageEntity> connect(String socketAddress);

  boolean subscribeToTimeline(String subscriptionType, String idStream, String filter);

  boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity);

  void closeSocket();
}
