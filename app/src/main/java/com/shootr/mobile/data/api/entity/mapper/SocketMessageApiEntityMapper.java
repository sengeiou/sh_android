package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.entity.BadgeContentEntity;
import com.shootr.mobile.data.entity.FixedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.FixedItemsSocketMessagesApiEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageEntity;
import com.shootr.mobile.data.entity.PinnedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.PinnedItemsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.SocketMessageEntity;
import com.shootr.mobile.data.entity.TimelineMessageApiEntity;
import com.shootr.mobile.data.entity.TimelineMessageEntity;
import com.shootr.mobile.data.entity.UpdateItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.UpdateItemSocketMessageEntity;
import javax.inject.Inject;

public class SocketMessageApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;
  private final TimelineApiEntityMapper timelineApiEntityMapper;
  private final DataApiEntityMapper dataApiEntityMapper;

  @Inject public SocketMessageApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      TimelineApiEntityMapper timelineApiEntityMapper, DataApiEntityMapper dataApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.timelineApiEntityMapper = timelineApiEntityMapper;
    this.dataApiEntityMapper = dataApiEntityMapper;
  }

  public SocketMessageEntity transform(SocketMessageApiEntity socketMessage) {

    if (socketMessage != null) {
      switch (socketMessage.getEventType()) {
        case SocketMessageApiEntity.TIMELINE:

          TimelineMessageEntity timelineMessageEntity = new TimelineMessageEntity();
          timelineMessageEntity.setEventType(socketMessage.getEventType());
          timelineMessageEntity.setVersion(socketMessage.getVersion());
          timelineMessageEntity.setRequestId(socketMessage.getRequestId());
          timelineMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          timelineMessageEntity.setData(
              timelineApiEntityMapper.map(((TimelineMessageApiEntity) socketMessage).getData()));

          return timelineMessageEntity;

        case SocketMessageApiEntity.NEW_ITEM_DATA:
          NewItemSocketMessageEntity newItemSocketMessageEntity = new NewItemSocketMessageEntity();
          newItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          newItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          newItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          newItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          newItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          newItemSocketMessageEntity.setData(shotApiEntityMapper.transform(
              (ShotApiEntity) ((NewItemSocketMessageApiEntity) socketMessage).getData()));

          return newItemSocketMessageEntity;

        case SocketMessageApiEntity.UPDATE_ITEM_DATA:
          UpdateItemSocketMessageEntity updateItemSocketMessageEntity =
              new UpdateItemSocketMessageEntity();
          updateItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          updateItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          updateItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          updateItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          updateItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          updateItemSocketMessageEntity.setData(shotApiEntityMapper.transform(
              (ShotApiEntity) ((UpdateItemSocketMessageApiEntity) socketMessage).getData()));

          return updateItemSocketMessageEntity;

        case SocketMessageApiEntity.FIXED_ITEMS:

          FixedItemSocketMessageEntity fixedItemSocketMessageEntity =
              new FixedItemSocketMessageEntity();
          fixedItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          fixedItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          fixedItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          fixedItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          fixedItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());
          fixedItemSocketMessageEntity.setData(dataApiEntityMapper.map(
              ((FixedItemsSocketMessagesApiEntity) socketMessage).getData()));

          return fixedItemSocketMessageEntity;

        case SocketMessageApiEntity.PINNED_ITEMS:

          PinnedItemSocketMessageEntity pinnedItemSocketMessageEntity =
              new PinnedItemSocketMessageEntity();
          pinnedItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          pinnedItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          pinnedItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          pinnedItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          pinnedItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          pinnedItemSocketMessageEntity.setData(dataApiEntityMapper.map(
              ((PinnedItemsSocketMessageApiEntity) socketMessage).getData()));

          return pinnedItemSocketMessageEntity;

        case SocketMessageApiEntity.PARTICIPANTS_UPDATE:

          ParticipantsSocketMessageEntity participantsSocketMessageEntity =
              new ParticipantsSocketMessageEntity();
          participantsSocketMessageEntity.setEventType(socketMessage.getEventType());
          participantsSocketMessageEntity.setVersion(socketMessage.getVersion());
          participantsSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          participantsSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);

          participantsSocketMessageEntity.setData(
              ((ParticipantsSocketMessageApiEntity) socketMessage).getData());

          return participantsSocketMessageEntity;

        case SocketMessageApiEntity.NEW_BADGE_CONTENT:

          NewBadgeContentSocketMessageEntity newBadgeContentSocketMessageEntity =
              new NewBadgeContentSocketMessageEntity();
          newBadgeContentSocketMessageEntity.setEventType(socketMessage.getEventType());
          newBadgeContentSocketMessageEntity.setVersion(socketMessage.getVersion());
          newBadgeContentSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          newBadgeContentSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);

          BadgeContentEntity badgeContentEntity = new BadgeContentEntity();

          badgeContentEntity.setBadgeType(
              ((NewBadgeContentSocketMessageApiEntity) socketMessage).getData().getBadgeType());
          badgeContentEntity.setFilter(
              ((NewBadgeContentSocketMessageApiEntity) socketMessage).getData().getFilter());

          newBadgeContentSocketMessageEntity.setData(badgeContentEntity);

          return newBadgeContentSocketMessageEntity;
        default:
          break;
      }
    }

    return null;
  }
}
