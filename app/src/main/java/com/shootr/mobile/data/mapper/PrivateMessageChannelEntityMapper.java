package com.shootr.mobile.data.mapper;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class PrivateMessageChannelEntityMapper {

  @Inject public PrivateMessageChannelEntityMapper() {

  }

  public PrivateMessageChannel transform(PrivateMessageChannelEntity privateMessageChannelEntity) {
    if (privateMessageChannelEntity == null) {
      return null;
    }
    PrivateMessageChannel privateMessageChannel = new PrivateMessageChannel();
    privateMessageChannel.setIdPrivateMessageChanel(
        privateMessageChannelEntity.getIdPrivateMessageChannel());
    privateMessageChannel.setIdTargetUser(privateMessageChannelEntity.getIdTargetUser());
    privateMessageChannel.setTitle(privateMessageChannelEntity.getTitle());
    privateMessageChannel.setImage(privateMessageChannelEntity.getImage());
    privateMessageChannel.setRead(privateMessageChannelEntity.getRead());
    if (privateMessageChannelEntity.getLastPrivateMessage() != null) {
      privateMessageChannel.setLastMessageTime(
          privateMessageChannelEntity.getLastPrivateMessage().getBirth().getTime());
      privateMessageChannel.setLastMessageComment(
          privateMessageChannelEntity.getLastPrivateMessage().getComment());
    }
    return privateMessageChannel;
  }

  public List<PrivateMessageChannel> transform(
      List<PrivateMessageChannelEntity> privateMessageChannelEntities) {
    List<PrivateMessageChannel> privateMessageChannels =
        new ArrayList<>(privateMessageChannelEntities.size());
    for (PrivateMessageChannelEntity privateMessageChannelEntity : privateMessageChannelEntities) {
      privateMessageChannels.add(transform(privateMessageChannelEntity));
    }
    return privateMessageChannels;
  }
}
