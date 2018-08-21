package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PartialUpdateItemSocketMessageEntity;
import com.shootr.mobile.data.entity.PromotedTiersSocketMessageEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.socket.CreatedShotSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ErrorSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.FixedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.NewBadgeContentSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.NewItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ParticipantsSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.PinnedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.PromotedTermsSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ShotDetailSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.ShotUpdateSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.SocketMessageEntity;
import com.shootr.mobile.data.entity.socket.StreamUpdateSocketMessageEntity;
import com.shootr.mobile.data.entity.socket.TimelineMessageEntity;
import com.shootr.mobile.data.entity.socket.UpdateItemSocketMessageEntity;
import com.shootr.mobile.domain.model.BadgeContent;
import com.shootr.mobile.domain.model.CreatedShotSocketMessage;
import com.shootr.mobile.domain.model.Error;
import com.shootr.mobile.domain.model.ErrorSocketMessage;
import com.shootr.mobile.domain.model.EventParams;
import com.shootr.mobile.domain.model.FixedItemSocketMessage;
import com.shootr.mobile.domain.model.Item;
import com.shootr.mobile.domain.model.NewBadgeContentSocketMessage;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.PartialUpdateItemSocketMessage;
import com.shootr.mobile.domain.model.Participants;
import com.shootr.mobile.domain.model.ParticipantsSocketMessage;
import com.shootr.mobile.domain.model.PinnedItemSocketMessage;
import com.shootr.mobile.domain.model.PromotedTerms;
import com.shootr.mobile.domain.model.PromotedTermsSocketMessage;
import com.shootr.mobile.domain.model.PromotedTiersSocketMessage;
import com.shootr.mobile.domain.model.ShotDetailSocketMessage;
import com.shootr.mobile.domain.model.ShotUpdateSocketMessage;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.StreamUpdateSocketMessage;
import com.shootr.mobile.domain.model.TimelineSocketMessage;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import com.shootr.mobile.domain.model.user.PromotedTiers;
import javax.inject.Inject;

public class SocketMessageEntityMapper {

  private final ShotEntityMapper shotEntityMapper;
  private final TimelineEntityMapper timelineEntityMapper;
  private final DataEntityMapper dataEntityMapper;
  private final ShotDetailEntityMapper shotDetailEntityMapper;
  private final PromotedTierEntityMapper promotedTierEntityMapper;
  private final PromotedReceiptEntityMapper promotedReceiptEntityMapper;
  private final PrintableEntityMapper printableEntityMapper;

  @Inject public SocketMessageEntityMapper(ShotEntityMapper shotApiEntityMapper,
      TimelineEntityMapper timelineEntityMapper, DataEntityMapper dataEntityMapper,
      ShotDetailEntityMapper shotDetailEntityMapper,
      PromotedTierEntityMapper promotedTierEntityMapper,
      PromotedReceiptEntityMapper promotedReceiptEntityMapper,
      PrintableEntityMapper printableEntityMapper) {
    this.shotEntityMapper = shotApiEntityMapper;
    this.timelineEntityMapper = timelineEntityMapper;
    this.dataEntityMapper = dataEntityMapper;
    this.shotDetailEntityMapper = shotDetailEntityMapper;
    this.promotedTierEntityMapper = promotedTierEntityMapper;
    this.promotedReceiptEntityMapper = promotedReceiptEntityMapper;
    this.printableEntityMapper = printableEntityMapper;
  }

  public SocketMessage transform(SocketMessageEntity socketMessage) {

    if (socketMessage != null) {
      switch (socketMessage.getEventType()) {
        case SocketMessage.TIMELINE:
          TimelineSocketMessage timelineSocketMessage = new TimelineSocketMessage();

          timelineSocketMessage.setEventType(socketMessage.getEventType());
          timelineSocketMessage.setVersion(socketMessage.getVersion());
          timelineSocketMessage.setRequestId(socketMessage.getRequestId());
          timelineSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          timelineSocketMessage.setData(
              timelineEntityMapper.map(((TimelineMessageEntity) socketMessage).getData()));

          return timelineSocketMessage;

        case SocketMessage.NEW_ITEM_DATA:
          NewItemSocketMessage newItemSocketMessage = new NewItemSocketMessage();

          newItemSocketMessage.setEventType(socketMessage.getEventType());
          newItemSocketMessage.setVersion(socketMessage.getVersion());
          newItemSocketMessage.setRequestId(socketMessage.getRequestId());
          newItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          newItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          Item item = new Item();
          item.setList(((NewItemSocketMessageEntity) socketMessage).getData().getList());

          item.setItem(printableEntityMapper.map(
              ((NewItemSocketMessageEntity) socketMessage).getData().getItem()));

          newItemSocketMessage.setData(item);

          return newItemSocketMessage;

        case SocketMessage.UPDATE_ITEM_DATA:
          UpdateItemSocketMessage updateItemSocketMessage = new UpdateItemSocketMessage();

          updateItemSocketMessage.setEventType(socketMessage.getEventType());
          updateItemSocketMessage.setVersion(socketMessage.getVersion());
          updateItemSocketMessage.setRequestId(socketMessage.getRequestId());
          updateItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          updateItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          item = new Item();
          item.setList(((UpdateItemSocketMessageEntity) socketMessage).getData().getList());

          item.setItem(printableEntityMapper.map(
              ((UpdateItemSocketMessageEntity) socketMessage).getData().getItem()));

          updateItemSocketMessage.setData(item);

          return updateItemSocketMessage;

        case SocketMessage.PARTIAL_UPDATE_ITEM_DATA:
          PartialUpdateItemSocketMessage partialUpdateItemSocketMessage = new PartialUpdateItemSocketMessage();

          partialUpdateItemSocketMessage.setEventType(socketMessage.getEventType());
          partialUpdateItemSocketMessage.setVersion(socketMessage.getVersion());
          partialUpdateItemSocketMessage.setRequestId(socketMessage.getRequestId());
          partialUpdateItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          partialUpdateItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          item = new Item();
          item.setList(((PartialUpdateItemSocketMessageEntity) socketMessage).getData().getList());

          item.setItem(printableEntityMapper.map(
              ((PartialUpdateItemSocketMessageEntity) socketMessage).getData().getItem()));

          partialUpdateItemSocketMessage.setData(item);

          return partialUpdateItemSocketMessage;

        case SocketMessage.FIXED_ITEMS:
          FixedItemSocketMessage fixedItemSocketMessage = new FixedItemSocketMessage();

          fixedItemSocketMessage.setEventType(socketMessage.getEventType());
          fixedItemSocketMessage.setVersion(socketMessage.getVersion());
          fixedItemSocketMessage.setRequestId(socketMessage.getRequestId());
          fixedItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          fixedItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          fixedItemSocketMessage.setData(
              dataEntityMapper.map(((FixedItemSocketMessageEntity) socketMessage).getData()));

          return fixedItemSocketMessage;

        case SocketMessage.PINNED_ITEMS:
          PinnedItemSocketMessage pinnedItemSocketMessage = new PinnedItemSocketMessage();

          pinnedItemSocketMessage.setEventType(socketMessage.getEventType());
          pinnedItemSocketMessage.setVersion(socketMessage.getVersion());
          pinnedItemSocketMessage.setRequestId(socketMessage.getRequestId());
          pinnedItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          pinnedItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          pinnedItemSocketMessage.setData(
              dataEntityMapper.map(((PinnedItemSocketMessageEntity) socketMessage).getData()));

          return pinnedItemSocketMessage;

        case SocketMessage.PARTICIPANTS_UPDATE:
          ParticipantsSocketMessage participantsSocketMessage = new ParticipantsSocketMessage();

          participantsSocketMessage.setEventType(socketMessage.getEventType());
          participantsSocketMessage.setVersion(socketMessage.getVersion());
          participantsSocketMessage.setRequestId(socketMessage.getRequestId());
          participantsSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          participantsSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));


          Participants participants = new Participants();
          participants.setFollowing(
              ((ParticipantsSocketMessageEntity) socketMessage).getData().getFollowing());
          participants.setTotal(
              ((ParticipantsSocketMessageEntity) socketMessage).getData().getTotal());

          participantsSocketMessage.setData(participants);

          return participantsSocketMessage;

        case SocketMessage.NEW_BADGE_CONTENT:
          NewBadgeContentSocketMessage newBadgeContentSocketMessage =
              new NewBadgeContentSocketMessage();
          newBadgeContentSocketMessage.setEventType(socketMessage.getEventType());
          newBadgeContentSocketMessage.setVersion(socketMessage.getVersion());
          newBadgeContentSocketMessage.setRequestId(socketMessage.getRequestId());
          newBadgeContentSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());

          BadgeContent badgeContent = new BadgeContent();

          badgeContent.setBadgeType(
              ((NewBadgeContentSocketMessageEntity) socketMessage).getData().getBadgeType());
          badgeContent.setFilter(
              ((NewBadgeContentSocketMessageEntity) socketMessage).getData().getFilter());

          newBadgeContentSocketMessage.setData(badgeContent);

          return newBadgeContentSocketMessage;
        case SocketMessage.SHOT_DETAIL:

          ShotDetailSocketMessage shotDetailSocketMessage = new ShotDetailSocketMessage();
          shotDetailSocketMessage.setEventType(socketMessage.getEventType());
          shotDetailSocketMessage.setVersion(socketMessage.getVersion());
          shotDetailSocketMessage.setRequestId(socketMessage.getRequestId());
          shotDetailSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());

          shotDetailSocketMessage.setData(shotDetailEntityMapper.transform(
              ((ShotDetailSocketMessageEntity) socketMessage).getData()));
          return shotDetailSocketMessage;

        case SocketMessage.SHOT_UPDATE:
          ShotUpdateSocketMessage shotUpdateSocketMessage = new ShotUpdateSocketMessage();

          shotUpdateSocketMessage.setEventType(socketMessage.getEventType());
          shotUpdateSocketMessage.setVersion(socketMessage.getVersion());
          shotUpdateSocketMessage.setRequestId(socketMessage.getRequestId());
          shotUpdateSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          shotUpdateSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          shotUpdateSocketMessage.setData(shotEntityMapper.transform(
              (ShotEntity) ((ShotUpdateSocketMessageEntity) socketMessage).getData()));

          return shotUpdateSocketMessage;

        case SocketMessage.CREATED_SHOT:
          CreatedShotSocketMessage createdShotSocketMessage = new CreatedShotSocketMessage();

          createdShotSocketMessage.setEventType(socketMessage.getEventType());
          createdShotSocketMessage.setVersion(socketMessage.getVersion());
          createdShotSocketMessage.setRequestId(socketMessage.getRequestId());
          createdShotSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());

          createdShotSocketMessage.setIdQueue(
              ((CreatedShotSocketMessageEntity) socketMessage).getIdQueue());

          return createdShotSocketMessage;

        case SocketMessage.ERROR:
          ErrorSocketMessage errorSocketMessage =
              new ErrorSocketMessage();
          errorSocketMessage.setEventType(socketMessage.getEventType());
          errorSocketMessage.setVersion(socketMessage.getVersion());
          errorSocketMessage.setRequestId(socketMessage.getRequestId());

          Error error = new Error();

          error.setCommand(((ErrorSocketMessageEntity) socketMessage).getData().getCommand());
          error.setErrorCode(((ErrorSocketMessageEntity) socketMessage).getData().getErrorCode());
          errorSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          errorSocketMessage.setData(error);

          return errorSocketMessage;

        case SocketMessage.PROMOTED_TIERS:
          PromotedTiersSocketMessage promotedTiersSocketMessage =
              new PromotedTiersSocketMessage();
          promotedTiersSocketMessage.setEventType(socketMessage.getEventType());
          promotedTiersSocketMessage.setVersion(socketMessage.getVersion());
          promotedTiersSocketMessage.setRequestId(socketMessage.getRequestId());

          PromotedTiers promotedTiers = new PromotedTiers();
          promotedTiers.setData(promotedTierEntityMapper.transform(
              ((PromotedTiersSocketMessageEntity) socketMessage).getData().getData()));
          promotedTiers.setPendingReceipts(promotedReceiptEntityMapper.transform(
              ((PromotedTiersSocketMessageEntity) socketMessage).getData().getPendingReceipts()));

          promotedTiersSocketMessage.setData(promotedTiers);

          return promotedTiersSocketMessage;

        case SocketMessage.STREAM_UPDATE:

          StreamUpdateSocketMessage streamUpdateSocketMessage = new StreamUpdateSocketMessage();
          streamUpdateSocketMessage.setEventType(socketMessage.getEventType());
          streamUpdateSocketMessage.setVersion(socketMessage.getVersion());
          streamUpdateSocketMessage.setRequestId(socketMessage.getRequestId());
          streamUpdateSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          streamUpdateSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          streamUpdateSocketMessage.setData(printableEntityMapper.map(
              ((StreamUpdateSocketMessageEntity) socketMessage).getData()));

          return streamUpdateSocketMessage;
        case SocketMessage.PROMOTED_TERMS:
          PromotedTermsSocketMessage promotedTermsSocketMessage =
              new PromotedTermsSocketMessage();

          promotedTermsSocketMessage.setEventType(socketMessage.getEventType());
          promotedTermsSocketMessage.setVersion(socketMessage.getVersion());
          promotedTermsSocketMessage.setRequestId(socketMessage.getRequestId());

          PromotedTerms promotedTerms = new PromotedTerms();

          promotedTerms.setTerms(((PromotedTermsSocketMessageEntity) socketMessage).getData().getTerms());
          promotedTerms.setVersion(((PromotedTermsSocketMessageEntity) socketMessage).getData().getVersion());


          promotedTermsSocketMessage.setData(promotedTerms);

          return promotedTermsSocketMessage;
        default:
          break;
      }
    }

    return null;
  }

  private EventParams transformParams(com.shootr.mobile.data.entity.EventParams eventParams) {
    EventParams params = new EventParams();
    try {
      params.setFilter(eventParams.getFilter());
      params.setIdShot(eventParams.getIdShot());
      params.setIdStream(eventParams.getIdStream());
      if (eventParams.getParams() != null && eventParams.getParams().getPeriod() != null) {
        params.setPeriod(eventParams.getParams().getPeriod().getDuration());
      }
    } catch (Exception e) {
      /* no-op */
    }

    return params;
  }
}
