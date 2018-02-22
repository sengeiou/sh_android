package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import io.reactivex.Observable;


public interface SocketApi {

  Observable<SocketMessageApiEntity> connect(String socketAddress);

  boolean subscribeToTimeline(String subscriptionType, String idStream, String filter);

  boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity);

  boolean sendNice(String idShot);

  void closeSocket();
}
