package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.api.entity.PrivateMessageTimelineEntity;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageTimeline;
import javax.inject.Inject;

public class PrivateMessageTimelineEntityMapper {

  public final PrivateMessageEntityMapper privateMessageEntityMapper;
  public final PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper;

  @Inject
  public PrivateMessageTimelineEntityMapper(PrivateMessageEntityMapper privateMessageEntityMapper,
      PrivateMessageChannelEntityMapper privateMessageChannelEntityMapper) {

    this.privateMessageEntityMapper = privateMessageEntityMapper;
    this.privateMessageChannelEntityMapper = privateMessageChannelEntityMapper;
  }

  public PrivateMessageTimeline transform(
      PrivateMessageTimelineEntity privateMessageTimelineEntity) {
    if (privateMessageTimelineEntity == null) {
      return null;
    }

    PrivateMessageTimeline privateMessageTimeline = new PrivateMessageTimeline();
    privateMessageTimeline.setPrivateMessageChannel(
        privateMessageChannelEntityMapper.transform(privateMessageTimelineEntity.getChannel()));
    privateMessageTimeline.setPrivateMessages(
        privateMessageEntityMapper.transform(privateMessageTimelineEntity.getMessages()));

    return privateMessageTimeline;
  }
}
