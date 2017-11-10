package com.shootr.mobile.ui.model.mappers;

import com.shootr.mobile.domain.model.privateMessageChannel.PrivateMessageChannel;
import com.shootr.mobile.ui.model.PrivateMessageChannelModel;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;


public class PrivateMessageChannelModelMapper {

  @Inject public PrivateMessageChannelModelMapper() {

  }

  public PrivateMessageChannelModel transform(PrivateMessageChannel privateMessageChannel) {

    PrivateMessageChannelModel model = new PrivateMessageChannelModel();

    model.setIdPrivateMessageChannel(privateMessageChannel.getIdPrivateMessageChanel());
    model.setIdTargetUser(privateMessageChannel.getIdTargetUser());
    model.setTitle(privateMessageChannel.getTitle());
    model.setImageUrl(privateMessageChannel.getImage());
    model.setRead(privateMessageChannel.isRead());
    model.setLastMessageTime(privateMessageChannel.getLastMessageTime());
    model.setLastMessageComment(privateMessageChannel.getLastMessageComment());
    model.setMuted(privateMessageChannel.isMuted());
    model.setFollowingTargetUser(privateMessageChannel.isFollowingTargetUser());

    return model;
  }

  public List<PrivateMessageChannelModel> transform(
      List<PrivateMessageChannel> privateMessageChannels) {
    List<PrivateMessageChannelModel> models = new ArrayList<>(privateMessageChannels.size());
    for (PrivateMessageChannel privateMessageChannel : privateMessageChannels) {
      models.add(transform(privateMessageChannel));
    }
    return models;
  }
}
