package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.api.SocketApi;
import com.shootr.mobile.data.api.entity.mapper.SocketMessageApiEntityMapper;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;
import io.reactivex.Observable;
import io.reactivex.functions.Function;
import javax.inject.Inject;

public class ServiceSocketDataSource implements SocketDataSource {

  private final SocketMessageApiEntityMapper socketMessageApiEntityMapper;
  private final SocketApi socketApi;

  @Inject public ServiceSocketDataSource(SocketMessageApiEntityMapper socketMessageApiEntityMapper,
      SocketApi socketApi) {
    this.socketMessageApiEntityMapper = socketMessageApiEntityMapper;
    this.socketApi = socketApi;
  }

  @Override public Observable<SocketMessageEntity> connect(String socketAddress) {
    return socketApi.connect(socketAddress)
        .map(new Function<SocketMessageApiEntity, SocketMessageEntity>() {
          @Override public SocketMessageEntity apply(SocketMessageApiEntity socketMessage)
              throws Exception {
            return socketMessageApiEntityMapper.transform(socketMessage);
          }
        });
  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter,
      long period) {
    return socketApi.subscribeToTimeline(subscriptionType, idStream, filter, period);
  }

  @Override public boolean subscribeToShotDetail(String subscriptionType, String idShot) {
    return socketApi.subscribeToShotDetail(subscriptionType, idShot);
  }

  @Override public void subscribeToPromotedTiers(String subscriptionType) {
    socketApi.subscribeToPromotedTiers(subscriptionType);
  }

  @Override
  public boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity) {
    return socketApi.getTimeline(idStream, filter, paginationEntity);
  }

  @Override public boolean getNicestTimeline(String idStream, String filter,
      PaginationEntity paginationEntity, ParamsEntity paramsEntity) {
    return socketApi.getNicestTimeline(idStream, filter, paginationEntity, paramsEntity);
  }

  @Override public boolean getShotDetail(String idShot, PaginationEntity promotedPagination,
      PaginationEntity subscribersPagination, PaginationEntity basicPagination) {
    return socketApi.getShotDetail(idShot, promotedPagination, subscribersPagination,
        basicPagination);
  }

  @Override public void closeSocket() {
    socketApi.closeSocket();
  }

  @Override
  public void updateSocketSubscription(String idStream, String filter, ParamsEntity paramsEntity) {
    socketApi.updateSocketSubscription(idStream, filter, paramsEntity);
  }

  @Override public void unsubscribeShotDetail(String idShot) {
    socketApi.unsubscribeShotDetail(idShot);
  }

  @Override public void getPromotedTiers() {
    socketApi.getPromotedTiers();
  }

  @Override public void verifyReceipt(String receipt) {
    socketApi.verifyReceipt(receipt);
  }

  @Override public void getPromotedTerms(String idStream) {
    socketApi.getPromotedTerms(idStream);
  }

  @Override public void markSeen(String type, String itemId) {
    socketApi.markSeen(type, itemId);
  }

  @Override public void acceptPromotedTerms(String idStream, int version) {
    socketApi.acceptPromotedTerms(idStream, version);
  }
}
