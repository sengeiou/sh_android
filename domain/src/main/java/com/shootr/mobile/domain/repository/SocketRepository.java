package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.model.SocketMessage;
import io.reactivex.Observable;

public interface SocketRepository {

  Observable<SocketMessage> connect(String socketAddress);

  boolean subscribeToTimeline(String subscriptionType, String idStream, String filter, long period);

  boolean getTimeline(String idStream, String filter, Pagination pagination);

  boolean getNicestTimeline(String idStream, String filter, Pagination pagination,
      long duration);

  void closeSocket();
}
