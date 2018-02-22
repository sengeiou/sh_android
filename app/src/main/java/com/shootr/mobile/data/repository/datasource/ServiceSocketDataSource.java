package com.shootr.mobile.data.repository.datasource;

import com.shootr.mobile.data.api.SocketApi;
import com.shootr.mobile.data.api.entity.mapper.SocketMessageApiEntityMapper;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SocketMessageEntity;
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
    return socketApi.connect(socketAddress).map(new Function<SocketMessageApiEntity, SocketMessageEntity>() {
      @Override public SocketMessageEntity apply(SocketMessageApiEntity socketMessage) throws Exception {
        return socketMessageApiEntityMapper.transform(socketMessage);
      }
    });

  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter) {
    return socketApi.subscribeToTimeline(subscriptionType, idStream, filter);
  }

  @Override public boolean getTimeline(String idStream, String filter, PaginationEntity paginationEntity) {
    return socketApi.getTimeline(idStream, filter, paginationEntity);
  }

  @Override public void closeSocket() {
    socketApi.closeSocket();
  }
}
