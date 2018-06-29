package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.entity.BadgeContentEntity;
import com.shootr.mobile.data.entity.CreatedShotSocketMessageApiEntity;
import com.shootr.mobile.data.entity.CreatedShotSocketMessageEntity;
import com.shootr.mobile.data.entity.ErrorEntity;
import com.shootr.mobile.data.entity.ErrorSocketMessageEntity;
import com.shootr.mobile.data.entity.ErrorSocketMessaggeApiEntity;
import com.shootr.mobile.data.entity.FixedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.FixedItemsSocketMessagesApiEntity;
import com.shootr.mobile.data.entity.ItemEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageEntity;
import com.shootr.mobile.data.entity.PinnedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.PinnedItemsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ShotDetailSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ShotDetailSocketMessageEntity;
import com.shootr.mobile.data.entity.ShotUpdateSocketMessageApiEntity;
import com.shootr.mobile.data.entity.ShotUpdateSocketMessageEntity;
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
  private final ShotDetailApiEntityMapper shotDetailApiEntityMapper;

  @Inject public SocketMessageApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      TimelineApiEntityMapper timelineApiEntityMapper, DataApiEntityMapper dataApiEntityMapper,
      ShotDetailApiEntityMapper shotDetailApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.timelineApiEntityMapper = timelineApiEntityMapper;
    this.dataApiEntityMapper = dataApiEntityMapper;
    this.shotDetailApiEntityMapper = shotDetailApiEntityMapper;
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

          ItemEntity itemEntity = new ItemEntity();
          itemEntity.setItem(shotApiEntityMapper.transform(
              (ShotApiEntity) ((NewItemSocketMessageApiEntity) socketMessage).getData().getItem()));
          itemEntity.setList(((NewItemSocketMessageApiEntity) socketMessage).getData().getList());

          newItemSocketMessageEntity.setData(itemEntity);

          return newItemSocketMessageEntity;

        case SocketMessageApiEntity.UPDATE_ITEM_DATA:
          UpdateItemSocketMessageEntity updateItemSocketMessageEntity =
              new UpdateItemSocketMessageEntity();
          updateItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          updateItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          updateItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          updateItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          updateItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          itemEntity = new ItemEntity();
          itemEntity.setItem(shotApiEntityMapper.transform(
              (ShotApiEntity) ((UpdateItemSocketMessageApiEntity) socketMessage).getData().getItem()));
          itemEntity.setList(((UpdateItemSocketMessageApiEntity) socketMessage).getData().getList());

          updateItemSocketMessageEntity.setData(itemEntity);

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

        case SocketMessageApiEntity.SHOT_DETAIL:

          ShotDetailSocketMessageEntity shotDetailSocketMessageEntity = new ShotDetailSocketMessageEntity();
          shotDetailSocketMessageEntity.setEventType(socketMessage.getEventType());
          shotDetailSocketMessageEntity.setVersion(socketMessage.getVersion());
          shotDetailSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          shotDetailSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);

          shotDetailSocketMessageEntity.setData(shotDetailApiEntityMapper.transform(
              ((ShotDetailSocketMessageApiEntity) socketMessage).getData()));

          return shotDetailSocketMessageEntity;

        case SocketMessageApiEntity.SHOT_UPDATE:
          ShotUpdateSocketMessageEntity shotUpdateSocketMessageEntity =
              new ShotUpdateSocketMessageEntity();
          shotUpdateSocketMessageEntity.setEventType(socketMessage.getEventType());
          shotUpdateSocketMessageEntity.setVersion(socketMessage.getVersion());
          shotUpdateSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          shotUpdateSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          shotUpdateSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          shotUpdateSocketMessageEntity.setData(shotApiEntityMapper.transform(
              (ShotApiEntity) ((ShotUpdateSocketMessageApiEntity) socketMessage).getData()));

          return shotUpdateSocketMessageEntity;

        case SocketMessageApiEntity.CREATED_SHOT:
          CreatedShotSocketMessageEntity createdShotSocketMessageEntity =
              new CreatedShotSocketMessageEntity();
          createdShotSocketMessageEntity.setEventType(socketMessage.getEventType());
          createdShotSocketMessageEntity.setVersion(socketMessage.getVersion());
          createdShotSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          createdShotSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);

          createdShotSocketMessageEntity.setIdQueue(
              ((CreatedShotSocketMessageApiEntity) socketMessage).getIdQueue());

          return createdShotSocketMessageEntity;

        case SocketMessageApiEntity.ERROR:
          ErrorSocketMessageEntity errorSocketMessageEntity =
              new ErrorSocketMessageEntity();
          errorSocketMessageEntity.setEventType(socketMessage.getEventType());
          errorSocketMessageEntity.setVersion(socketMessage.getVersion());
          errorSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          errorSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          errorSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          ErrorEntity errorEntity = new ErrorEntity();

          errorEntity.setCommand(((ErrorSocketMessaggeApiEntity) socketMessage).getData().getCommand());
          errorEntity.setErrorCode(((ErrorSocketMessaggeApiEntity) socketMessage).getData().getErrorCode());

          errorSocketMessageEntity.setData(errorEntity);

          return errorSocketMessageEntity;
        default:
          break;
      }
    }

    return null;
  }
}
