package com.shootr.mobile.domain.repository;

import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.model.SocketMessage;
import io.reactivex.Observable;

public interface SocketRepository {

  Observable<SocketMessage> connect(String socketAddress);

  boolean subscribeToTimeline(String subscriptionType, String idStream, String filter, long period);

  boolean subscribeToShotDetail(String subscriptionType, String idShot);

  void subscribeToPromotedTiers(String subscriptionType);

  boolean getTimeline(String idStream, String filter, Pagination pagination);

  boolean getNicestTimeline(String idStream, String filter, Pagination pagination,
      long duration);

  boolean getShotDetail(String idShot, Pagination promotedPagination,
      Pagination subscribersPagination, Pagination basicPagination);

  void unsubscribeShotDetail(String idShot);

  void closeSocket();

  void getPromotedTiers();

  void verifyReceipt(String receipt);

  void markSeen(String type, String itemId);

  void getPromotedTerms(String idStream);
}
