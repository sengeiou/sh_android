package com.shootr.mobile.data.api;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import io.reactivex.Observable;


public interface SocketApi {

  Observable<SocketMessageApiEntity> connect(String socketAddress);

  boolean subscribeToTimeline(String subscriptionType, String idStream, String filter, long period);

  boolean subscribeToShotDetail(String subscriptionType, String idShot);

  boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity);

  boolean getNicestTimeline(String idStream, String filter, PaginationEntity paginationEntity,
      ParamsEntity paramsEntity);

  boolean sendNice(String idShot);

  void closeSocket();

  void updateSocketSubscription(String idStream, String filter, ParamsEntity paramsEntity);

  boolean getShotDetail(String idShot, PaginationEntity promotedPagination,
      PaginationEntity subscribersPagination, PaginationEntity basicPagination);

  void unsubscribeShotDetail(String idShot);
}
