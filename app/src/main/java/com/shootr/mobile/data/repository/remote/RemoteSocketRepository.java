package com.shootr.mobile.data.repository.remote;

import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.SocketMessageEntity;
import com.shootr.mobile.data.mapper.SocketMessageEntityMapper;
import com.shootr.mobile.data.repository.datasource.SocketDataSource;
import com.shootr.mobile.data.repository.remote.cache.TimelineCache;
import com.shootr.mobile.data.repository.remote.cache.TimelineRepositionCache;
import com.shootr.mobile.domain.model.FixedItemSocketMessage;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.model.PinnedItemSocketMessage;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.TimelineSocketMessage;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import javax.inject.Inject;

public class RemoteSocketRepository implements SocketRepository {

  private final SocketDataSource socketDataSource;
  private final SocketMessageEntityMapper socketEntityMapper;
  private final TimelineCache timelineCache;
  private final TimelineRepositionCache timelineRepositionCache;

  @Inject public RemoteSocketRepository(@Remote SocketDataSource socketDataSource,
      SocketMessageEntityMapper socketEntityMapper, TimelineCache timelineCache,
      TimelineRepositionCache timelineRepositionCache) {
    this.socketDataSource = socketDataSource;
    this.socketEntityMapper = socketEntityMapper;
    this.timelineCache = timelineCache;
    this.timelineRepositionCache = timelineRepositionCache;
  }


  @Override public Observable<SocketMessage> connect(final String socketAddress) {
    return socketDataSource.connect(socketAddress).map(new Function<SocketMessageEntity, SocketMessage>() {
      @Override public SocketMessage apply(SocketMessageEntity socketMessageEntity)
          throws Exception {
        return socketEntityMapper.transform(socketMessageEntity);
      }
    }).doOnNext(new Consumer<SocketMessage>() {
      @Override public void accept(SocketMessage socketMessage) throws Exception {
        synchronized (timelineCache) {
          if (socketMessage.getEventType().equals(SocketMessage.TIMELINE)) {
            timelineCache.putTimeline(((TimelineSocketMessage) socketMessage).getData());
            ((TimelineSocketMessage) socketMessage).getData()
                .setTimelineReposition(timelineRepositionCache.getTimelineReposition(
                    ((TimelineSocketMessage) socketMessage).getData().getStream().getId(),
                    ((TimelineSocketMessage) socketMessage).getData().getFilter()));
          } else if (socketMessage.getEventType().equals(SocketMessage.NEW_ITEM_DATA)) {
            timelineCache.putItemInTimeline(((NewItemSocketMessage) socketMessage).getData(),
                socketMessage.getEventParams().getFilter());
          } else if (socketMessage.getEventType().equals(SocketMessage.UPDATE_ITEM_DATA)) {
            timelineCache.updateItem(((UpdateItemSocketMessage) socketMessage).getData(),
                socketMessage.getEventParams().getFilter());
          } else if (socketMessage.getEventType().equals(SocketMessage.FIXED_ITEMS)) {
            timelineCache.putFixedItem(((FixedItemSocketMessage) socketMessage).getData(),
                socketMessage.getEventParams().getIdStream(),
                socketMessage.getEventParams().getFilter());
          } else if (socketMessage.getEventType().equals(SocketMessage.PINNED_ITEMS)) {
            timelineCache.putPinnedItem(((PinnedItemSocketMessage) socketMessage).getData(),
                socketMessage.getEventParams().getIdStream(),
                socketMessage.getEventParams().getFilter());
          }
        }
      }
    });
  }

  @Override
  public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter) {
    return socketDataSource.subscribeToTimeline(subscriptionType, idStream, filter);
  }

  @Override public boolean getTimeline(String idStream, String filter, Pagination pagination) {
    PaginationEntity paginationEntity = new PaginationEntity();
    paginationEntity.setMaxTimestamp(pagination.getMaxTimestamp());
    paginationEntity.setSinceTimestamp(pagination.getSinceTimestamp());
    return socketDataSource.getTimeline(idStream, filter, paginationEntity);
  }

  @Override public void closeSocket() {
    socketDataSource.closeSocket();
  }
}
