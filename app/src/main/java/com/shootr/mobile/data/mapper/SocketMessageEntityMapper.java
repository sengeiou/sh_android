package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.FixedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.NewBadgeContentSocketMessageEntity;
import com.shootr.mobile.data.entity.NewItemSocketMessageEntity;
import com.shootr.mobile.data.entity.ParticipantsSocketMessageEntity;
import com.shootr.mobile.data.entity.PinnedItemSocketMessageEntity;
import com.shootr.mobile.data.entity.ShotEntity;
import com.shootr.mobile.data.entity.SocketMessageEntity;
import com.shootr.mobile.data.entity.TimelineMessageEntity;
import com.shootr.mobile.data.entity.UpdateItemSocketMessageEntity;
import com.shootr.mobile.domain.model.BadgeContent;
import com.shootr.mobile.domain.model.EventParams;
import com.shootr.mobile.domain.model.FixedItemSocketMessage;
import com.shootr.mobile.domain.model.NewBadgeContentSocketMessage;
import com.shootr.mobile.domain.model.NewItemSocketMessage;
import com.shootr.mobile.domain.model.Participants;
import com.shootr.mobile.domain.model.ParticipantsSocketMessage;
import com.shootr.mobile.domain.model.PinnedItemSocketMessage;
import com.shootr.mobile.domain.model.SocketMessage;
import com.shootr.mobile.domain.model.TimelineSocketMessage;
import com.shootr.mobile.domain.model.UpdateItemSocketMessage;
import javax.inject.Inject;

public class SocketMessageEntityMapper {

  private final ShotEntityMapper shotEntityMapper;
  private final TimelineEntityMapper timelineEntityMapper;
  private final DataEntityMapper dataEntityMapper;

  @Inject public SocketMessageEntityMapper(ShotEntityMapper shotApiEntityMapper,
      TimelineEntityMapper timelineEntityMapper, DataEntityMapper dataEntityMapper) {
    this.shotEntityMapper = shotApiEntityMapper;
    this.timelineEntityMapper = timelineEntityMapper;
    this.dataEntityMapper = dataEntityMapper;
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

          newItemSocketMessage.setData(shotEntityMapper.transform(
              (ShotEntity) ((NewItemSocketMessageEntity) socketMessage).getData()));

          return newItemSocketMessage;

        case SocketMessage.UPDATE_ITEM_DATA:
          UpdateItemSocketMessage updateItemSocketMessage = new UpdateItemSocketMessage();

          updateItemSocketMessage.setEventType(socketMessage.getEventType());
          updateItemSocketMessage.setVersion(socketMessage.getVersion());
          updateItemSocketMessage.setRequestId(socketMessage.getRequestId());
          updateItemSocketMessage.setActiveSubscription(socketMessage.isActiveSubscription());
          updateItemSocketMessage.setEventParams(transformParams(socketMessage.getEventParams()));

          updateItemSocketMessage.setData(shotEntityMapper.transform(
              (ShotEntity) ((UpdateItemSocketMessageEntity) socketMessage).getData()));

          return updateItemSocketMessage;

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
        default:
          break;
      }
    }

    return null;
  }

  private EventParams transformParams(com.shootr.mobile.data.entity.EventParams eventParams) {
    EventParams params = new EventParams();

    params.setFilter(eventParams.getFilter());
    params.setIdShot(eventParams.getIdShot());
    params.setIdStream(eventParams.getIdStream());
    if (eventParams.getParams() != null) {
      params.setPeriod(eventParams.getParams().getPeriod().getDuration());
    }

    return params;
  }
}
