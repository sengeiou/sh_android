package com.shootr.mobile.data.api.entity.mapper;

import com.shootr.mobile.data.api.entity.ShotApiEntity;
import com.shootr.mobile.data.entity.BadgeContentEntity;
import com.shootr.mobile.data.entity.ErrorEntity;
import com.shootr.mobile.data.entity.PartialUpdateItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PartialUpdateItemSocketMessageEntity;
import com.shootr.mobile.data.entity.PromotedTermsEntity;
import com.shootr.mobile.data.entity.PromotedTiersEntity;
import com.shootr.mobile.data.entity.PromotedTiersSocketMessageApiEntity;
import com.shootr.mobile.data.entity.PromotedTiersSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.CreatedShotSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.CreatedShotSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ErrorSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ErrorSocketMessaggeApiEntity;
import com.shootr.mobile.data.entity.socket.FixedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.FixedItemsSocketMessagesApiEntity;
import com.shootr.mobile.data.entity.socket.ItemEntity;
import com.shootr.mobile.data.entity.socket.NewBadgeContentSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.NewBadgeContentSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.NewItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.NewItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ParticipantsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.ParticipantsSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.PinnedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.PinnedItemsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.PromotedTermsSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.PromotedTermsSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ShotDetailSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.ShotDetailSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ShotUpdateSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.ShotUpdateSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;
import com.shootr.mobile.data.entity.socket.TimelineMessageApiEntity;
import com.shootr.mobile.data.entity.socket.TimelineMessageEntity;
import com.shootr.mobile.data.entity.socket.UpdateItemSocketMessageApiEntity;
import com.shootr.mobile.data.entity.socket.UpdateItemSocketMessageEntity;
import javax.inject.Inject;

public class SocketMessageApiEntityMapper {

  private final ShotApiEntityMapper shotApiEntityMapper;
  private final TimelineApiEntityMapper timelineApiEntityMapper;
  private final DataApiEntityMapper dataApiEntityMapper;
  private final ShotDetailApiEntityMapper shotDetailApiEntityMapper;
  private final PromotedTierApiEntityMapper promotedTierApiEntityMapper;
  private final PromotedReceiptApiEntityMapper promotedReceiptApiEntityMapper;
  private final PrintableItemApiEntityMapper printableItemApiEntityMapper;

  @Inject public SocketMessageApiEntityMapper(ShotApiEntityMapper shotApiEntityMapper,
      TimelineApiEntityMapper timelineApiEntityMapper, DataApiEntityMapper dataApiEntityMapper,
      ShotDetailApiEntityMapper shotDetailApiEntityMapper,
      PromotedTierApiEntityMapper promotedTierApiEntityMapper,
      PromotedReceiptApiEntityMapper promotedReceiptApiEntityMapper,
      PrintableItemApiEntityMapper printableItemApiEntityMapper) {
    this.shotApiEntityMapper = shotApiEntityMapper;
    this.timelineApiEntityMapper = timelineApiEntityMapper;
    this.dataApiEntityMapper = dataApiEntityMapper;
    this.shotDetailApiEntityMapper = shotDetailApiEntityMapper;
    this.promotedTierApiEntityMapper = promotedTierApiEntityMapper;
    this.promotedReceiptApiEntityMapper = promotedReceiptApiEntityMapper;
    this.printableItemApiEntityMapper = printableItemApiEntityMapper;
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
          itemEntity.setList(((NewItemSocketMessageApiEntity) socketMessage).getData().getList());

          itemEntity.setItem(printableItemApiEntityMapper.map(
              ((NewItemSocketMessageApiEntity) socketMessage).getData().getItem()));

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

          itemEntity.setList(((UpdateItemSocketMessageApiEntity) socketMessage).getData().getList());

          itemEntity.setItem(printableItemApiEntityMapper.map(
              ((UpdateItemSocketMessageApiEntity) socketMessage).getData().getItem()));

          updateItemSocketMessageEntity.setData(itemEntity);

          return updateItemSocketMessageEntity;

        case SocketMessageApiEntity.PARTIAL_UPDATE_ITEM_DATA:
          PartialUpdateItemSocketMessageEntity partialUpdateItemSocketMessageEntity =
              new PartialUpdateItemSocketMessageEntity();
          partialUpdateItemSocketMessageEntity.setEventType(socketMessage.getEventType());
          partialUpdateItemSocketMessageEntity.setVersion(socketMessage.getVersion());
          partialUpdateItemSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          partialUpdateItemSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          partialUpdateItemSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          itemEntity = new ItemEntity();

          itemEntity.setList(((PartialUpdateItemSocketMessageApiEntity) socketMessage).getData().getList());

          itemEntity.setItem(printableItemApiEntityMapper.map(
              ((PartialUpdateItemSocketMessageApiEntity) socketMessage).getData().getItem()));

          partialUpdateItemSocketMessageEntity.setData(itemEntity);

          return partialUpdateItemSocketMessageEntity;

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
        case SocketMessageApiEntity.PROMOTED_TIERS:
          PromotedTiersSocketMessageEntity promotedTiersSocketMessageEntity =
              new PromotedTiersSocketMessageEntity();
          promotedTiersSocketMessageEntity.setEventType(socketMessage.getEventType());
          promotedTiersSocketMessageEntity.setVersion(socketMessage.getVersion());
          promotedTiersSocketMessageEntity.setRequestId(socketMessage.getRequestId());

          PromotedTiersEntity promotedTiersEntity = new PromotedTiersEntity();

          promotedTiersEntity.setData(promotedTierApiEntityMapper.transform(
              ((PromotedTiersSocketMessageApiEntity) socketMessage).getData().getData()));

          promotedTiersEntity.setPendingReceipts(promotedReceiptApiEntityMapper.transform(
              ((PromotedTiersSocketMessageApiEntity) socketMessage).getData()
                  .getPendingReceipts()));

          promotedTiersSocketMessageEntity.setData(promotedTiersEntity);

          return promotedTiersSocketMessageEntity;

        case SocketMessageApiEntity.PROMOTED_TERMS:
          PromotedTermsSocketMessageEntity promotedTermsSocketMessageEntity =
              new PromotedTermsSocketMessageEntity();
          promotedTermsSocketMessageEntity.setEventType(socketMessage.getEventType());
          promotedTermsSocketMessageEntity.setVersion(socketMessage.getVersion());
          promotedTermsSocketMessageEntity.setRequestId(socketMessage.getRequestId());
          promotedTermsSocketMessageEntity.setActiveSubscription(socketMessage.isActiveSubscription);
          promotedTermsSocketMessageEntity.setEventParams(socketMessage.getEventParams());

          PromotedTermsEntity promotedTermsEntity = new PromotedTermsEntity();

          promotedTermsEntity.setTerms(((PromotedTermsSocketMessageApiEntity) socketMessage).getData().getTerms());
          promotedTermsEntity.setVersion(((PromotedTermsSocketMessageApiEntity) socketMessage).getData().getVersion());

          promotedTermsSocketMessageEntity.setData(promotedTermsEntity);

          return promotedTermsSocketMessageEntity;
        default:
          break;
      }
    }

    return null;
  }
}
