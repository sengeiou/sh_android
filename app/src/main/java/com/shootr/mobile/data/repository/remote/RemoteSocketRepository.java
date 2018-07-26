package com.shootr.mobile.data.repository.remote;

import android.support.annotation.NonNull;
import com.shootr.mobile.data.api.exception.ErrorInfo;
import com.shootr.mobile.data.entity.PaginationEntity;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.PeriodEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;
import com.shootr.mobile.data.entity.socket.TimelineMessageEntity;
import com.shootr.mobile.data.mapper.SocketMessageEntityMapper;
import com.shootr.mobile.data.repository.datasource.SocketDataSource;
import com.shootr.mobile.data.repository.remote.cache.PromotedTiersCache;
import com.shootr.mobile.data.repository.remote.cache.ShotDetailCache;
import com.shootr.mobile.data.repository.remote.cache.TimelineCache;
import com.shootr.mobile.data.repository.remote.cache.TimelineRepositionCache;
import com.shootr.mobile.domain.bus.BusPublisher;
import com.shootr.mobile.domain.bus.ShotFailed;
import com.shootr.mobile.domain.model.CreatedShotSocketMessage;
import com.shootr.mobile.domain.model.ErrorSocketMessage;
import com.shootr.mobile.domain.model.FixedItemSocketMessage;
import com.shootr.mobile.domain.model.ListType;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.Pagination;
import com.shootr.mobile.domain.model.PartialUpdateItemSocketMessage;
import com.shootr.mobile.domain.model.PinnedItemSocketMessage;
import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.PromotedReceipt;
import com.shootr.mobile.domain.model.PromotedTiersSocketMessage;
import com.shootr.mobile.domain.model.ShotDetailSocketMessage;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.TimelineSocketMessage;
import com.shootr.mobile.domain.model.TimelineType;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import com.shootr.mobile.domain.model.shot.QueuedShot;
import com.shootr.mobile.domain.repository.Remote;
import com.shootr.mobile.domain.repository.SocketRepository;
import com.shootr.mobile.domain.service.QueueRepository;
import com.shootr.mobile.domain.service.ShotQueueListener;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import javax.inject.Inject;

public class RemoteSocketRepository implements SocketRepository {

  private final SocketDataSource socketDataSource;
  private final SocketMessageEntityMapper socketEntityMapper;
  private final TimelineCache timelineCache;
  private final TimelineRepositionCache timelineRepositionCache;
  private final ShotDetailCache shotDetailCache;
  private final QueueRepository queueRepository;
  private final ShotQueueListener shotQueueListener;
  private final BusPublisher busPublisher;
  private final PromotedTiersCache promotedTiersCache;


  @Inject public RemoteSocketRepository(@Remote SocketDataSource socketDataSource,
      SocketMessageEntityMapper socketEntityMapper, TimelineCache timelineCache,
      TimelineRepositionCache timelineRepositionCache, ShotDetailCache shotDetailCache,
      QueueRepository queueRepository, ShotQueueListener shotQueueListener,
      BusPublisher busPublisher, PromotedTiersCache promotedTiersCache) {
    this.socketDataSource = socketDataSource;
    this.socketEntityMapper = socketEntityMapper;
    this.timelineCache = timelineCache;
    this.timelineRepositionCache = timelineRepositionCache;
    this.shotDetailCache = shotDetailCache;
    this.queueRepository = queueRepository;
    this.shotQueueListener = shotQueueListener;
    this.busPublisher = busPublisher;
    this.promotedTiersCache = promotedTiersCache;
  }

  @Override public Observable<SocketMessage> connect(final String socketAddress) {
    return socketDataSource.connect(socketAddress).doOnNext(new Consumer<SocketMessageEntity>() {
      @Override public void accept(SocketMessageEntity socketMessageEntity) throws Exception {
        if (socketMessageEntity.getEventType().equals(SocketMessage.TIMELINE)) {
          if (((TimelineMessageEntity) socketMessageEntity).getData()
              .getFilter()
              .equals(TimelineType.NICEST)) {
            socketDataSource.updateSocketSubscription(
                ((TimelineMessageEntity) socketMessageEntity).getData().getStream().getIdStream(),
                ((TimelineMessageEntity) socketMessageEntity).getData().getFilter(),
                ((TimelineMessageEntity) socketMessageEntity).getData().getParams());
          }
        }
      }
    }).map(new Function<SocketMessageEntity, SocketMessage>() {
      @Override public SocketMessage apply(SocketMessageEntity socketMessageEntity)
          throws Exception {
        return socketEntityMapper.transform(socketMessageEntity);
      }
    }).doOnNext(new Consumer<SocketMessage>() {
      @Override public void accept(SocketMessage socketMessage) throws Exception {
        synchronized (timelineCache) {
          switch (socketMessage.getEventType()) {
            case SocketMessage.TIMELINE:
              timelineCache.putTimeline(((TimelineSocketMessage) socketMessage).getData());
              ((TimelineSocketMessage) socketMessage).getData()
                  .setTimelineReposition(timelineRepositionCache.getTimelineReposition(
                      ((TimelineSocketMessage) socketMessage).getData().getStream().getId(),
                      ((TimelineSocketMessage) socketMessage).getData().getFilter()));
              break;
            case SocketMessage.NEW_ITEM_DATA:
              if (((NewItemSocketMessage) socketMessage).getData()
                  .getItem()
                  .getResultType()
                  .equals(PrintableType.PROMOTED_RECEIPT)) {
                promotedTiersCache.addPromotedReceipt(
                    (PromotedReceipt) ((NewItemSocketMessage) socketMessage).getData().getItem());
                break;
              }

              if (ListType.TIMELINE_TYPES.contains(((NewItemSocketMessage) socketMessage).getData()
                  .getList())) {
                timelineCache.putItemInTimeline(
                    ((NewItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getIdStream(),
                    socketMessage.getEventParams().getFilter(),
                    ((NewItemSocketMessage) socketMessage).getData().getList());
              }

              shotDetailCache.addItemInShotDetail(
                  ((NewItemSocketMessage) socketMessage).getData().getItem(),
                  ((NewItemSocketMessage) socketMessage).getData().getList());
              break;
            case SocketMessage.UPDATE_ITEM_DATA:

              if (((UpdateItemSocketMessage) socketMessage).getData()
                  .getItem()
                  .getResultType()
                  .equals(PrintableType.PROMOTED_RECEIPT)) {
                promotedTiersCache.updatePromotedReceipt(
                    (PromotedReceipt) ((UpdateItemSocketMessage) socketMessage).getData()
                        .getItem());
                break;
              }

              if (socketMessage.getEventParams().getFilter() != null
                  && socketMessage.getEventParams().getFilter().equals(TimelineType.NICEST)) {
                timelineCache.updateItemInNicestTimeline(
                    ((UpdateItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getFilter(),
                    socketMessage.getEventParams().getPeriod());
                break;
              }

              if (ListType.TIMELINE_TYPES.contains(
                  ((UpdateItemSocketMessage) socketMessage).getData().getList())) {
                timelineCache.updateItem(
                    ((UpdateItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getIdStream(),
                    socketMessage.getEventParams().getFilter(),
                    ((UpdateItemSocketMessage) socketMessage).getData().getList());
              }

              if (socketMessage.getEventParams().getIdShot() != null) {
                shotDetailCache.updateItem(
                    ((UpdateItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getIdShot(),
                    ((UpdateItemSocketMessage) socketMessage).getData().getList());
              }

              break;

            case SocketMessage.PARTIAL_UPDATE_ITEM_DATA:

              /*if (((PartialUpdateItemSocketMessage) socketMessage).getData()
                  .getItem()
                  .getResultType()
                  .equals(PrintableType.PROMOTED_RECEIPT)) {
                promotedTiersCache.updatePromotedReceipt(
                    (PromotedReceipt) ((PartialUpdateItemSocketMessage) socketMessage).getData()
                        .getItem());
                break;
              }*/

              /*if (socketMessage.getEventParams().getFilter() != null
                  && socketMessage.getEventParams().getFilter().equals(TimelineType.NICEST)) {
                timelineCache.updateItemInNicestTimeline(
                    ((PartialUpdateItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getFilter(),
                    socketMessage.getEventParams().getPeriod());
                break;
              }*/

              PrintableItem printableItem = timelineCache.partialUpdateItem(
                  ((PartialUpdateItemSocketMessage) socketMessage).getData().getItem(),
                  socketMessage.getEventParams().getIdStream(),
                  socketMessage.getEventParams().getFilter(),
                  ((PartialUpdateItemSocketMessage) socketMessage).getData().getList());

              ((PartialUpdateItemSocketMessage) socketMessage).getData().setItem(printableItem);

              /*if (socketMessage.getEventParams().getIdShot() != null) {
                shotDetailCache.updateItem(
                    ((PartialUpdateItemSocketMessage) socketMessage).getData().getItem(),
                    socketMessage.getEventParams().getIdShot(),
                    ((PartialUpdateItemSocketMessage) socketMessage).getData().getList());
              }*/

              break;

            case SocketMessage.FIXED_ITEMS:
              timelineCache.putFixedItem(((FixedItemSocketMessage) socketMessage).getData(),
                  socketMessage.getEventParams().getIdStream(),
                  socketMessage.getEventParams().getFilter());
              break;
            case SocketMessage.PINNED_ITEMS:
              timelineCache.putPinnedItem(((PinnedItemSocketMessage) socketMessage).getData(),
                  socketMessage.getEventParams().getIdStream(),
                  socketMessage.getEventParams().getFilter());
              break;
            case SocketMessage.SHOT_DETAIL:
              shotDetailCache.putShot(((ShotDetailSocketMessage) socketMessage).getData());
              break;
            case SocketMessage.SHOT_UPDATE:
              shotDetailCache.updateItem(
                  ((UpdateItemSocketMessage) socketMessage).getData().getItem(),
                  socketMessage.getEventParams().getIdShot(),
                  ((UpdateItemSocketMessage) socketMessage).getData().getList());
              break;
            case SocketMessage.CREATED_SHOT:
              queueRepository.remove(((CreatedShotSocketMessage) socketMessage).getIdQueue());
              break;
            case SocketMessage.ERROR:
              handleError((ErrorSocketMessage) socketMessage);
              break;
            case SocketMessage.PROMOTED_TIERS:
              promotedTiersCache.putPromotedTiers(
                  ((PromotedTiersSocketMessage) socketMessage).getData());
              break;
            default:
              break;
          }
        }
      }
    });
  }

  @Override public boolean subscribeToTimeline(String subscriptionType, String idStream, String filter,
      long period) {
    return socketDataSource.subscribeToTimeline(subscriptionType, idStream, filter, period);
  }

  @Override public boolean subscribeToShotDetail(String subscriptionType, String idShot) {
    return socketDataSource.subscribeToShotDetail(subscriptionType, idShot);
  }

  @Override public void subscribeToPromotedTiers(String subscriptionType) {
    socketDataSource.subscribeToPromotedTiers(subscriptionType);
  }

  @Override public boolean getTimeline(String idStream, String filter, Pagination pagination) {
    PaginationEntity paginationEntity = transformPagination(pagination);
    return socketDataSource.getTimeline(idStream, filter, paginationEntity);
  }

  @Override public boolean getNicestTimeline(String idStream, String filter, Pagination pagination,
      long duration) {
    PaginationEntity paginationEntity = transformPagination(pagination);

    PeriodEntity periodEntity = new PeriodEntity();
    periodEntity.setDuration(duration);

    ParamsEntity paramsEntity = new ParamsEntity();
    paramsEntity.setPeriod(periodEntity);

    return socketDataSource.getNicestTimeline(idStream, filter, paginationEntity, paramsEntity);
  }

  @Override public boolean getShotDetail(String idShot, Pagination promotedPagination,
      Pagination subscribersPagination, Pagination basicPagination) {

    return socketDataSource.getShotDetail(idShot, transformPagination(promotedPagination),
        transformPagination(subscribersPagination), transformPagination(basicPagination));
  }

  @Override public void unsubscribeShotDetail(String idShot) {
    socketDataSource.unsubscribeShotDetail(idShot);
  }

  @Override public void closeSocket() {
    socketDataSource.closeSocket();
  }

  @Override public void getPromotedTiers() {
    socketDataSource.getPromotedTiers();
  }

  @Override public void verifyReceipt(String receipt) {
    socketDataSource.verifyReceipt(receipt);
  }

  @Override public void markSeen(String type, String itemId) {
    socketDataSource.markSeen(type, itemId);
  }

  @NonNull private PaginationEntity transformPagination(Pagination pagination) {
    PaginationEntity paginationEntity = new PaginationEntity();
    paginationEntity.setMaxTimestamp(pagination.getMaxTimestamp());
    paginationEntity.setSinceTimestamp(pagination.getSinceTimestamp());
    return paginationEntity;
  }

  private void handleError(ErrorSocketMessage errorMessage) {
    if (errorMessage.getData().getErrorCode() == ErrorInfo.CODE_RESOURCE_NOT_FOUND) {
      notifyShotSendingHasDeletedParent(
          clearShotFromQueue(errorMessage.getEventParams().getIdShot()));
    } else if (errorMessage.getData().getErrorCode() == ErrorInfo.CODE_STREAM_REMOVED) {
      notifyShotSendingHasRemovedStream(
          clearShotFromQueue(errorMessage.getEventParams().getIdShot()));
    } else if (errorMessage.getData().getErrorCode() == ErrorInfo.CODE_STREAM_VIEW_ONLY) {
      notifyShotSendingHasReadOnlyStream(
          clearShotFromQueue(errorMessage.getEventParams().getIdShot()));
    } else if (errorMessage.getData().getErrorCode() == ErrorInfo.CODE_INVALID_SHOT_RECEIPT) {
      clearShotFromQueue(errorMessage.getEventParams().getIdShot());
    }
  }

  private QueuedShot clearShotFromQueue(String idQueued) {
    QueuedShot queuedShot = queueRepository.getQueue(Long.valueOf(idQueued), QueueRepository.SHOT_TYPE);
    queueRepository.remove(idQueued);
    return queuedShot;
  }

  private void notifyShotSendingHasDeletedParent(QueuedShot queuedShot) {
    shotQueueListener.onShotHasParentDeleted(queuedShot);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasRemovedStream(QueuedShot queuedShot) {
    shotQueueListener.onShotHasStreamRemoved(queuedShot);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }

  private void notifyShotSendingHasReadOnlyStream(QueuedShot queuedShot) {
    shotQueueListener.onShotIsOnReadOnly(queuedShot);
    busPublisher.post(new ShotFailed.Event(queuedShot.getBaseMessage()));
  }
}

