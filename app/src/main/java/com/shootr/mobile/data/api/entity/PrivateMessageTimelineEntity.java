package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.PrivateMessageChannelEntity;
import com.shootr.mobile.data.entity.PrivateMessageEntity;
import java.util.List;

public class PrivateMessageTimelineEntity {

  private PrivateMessageChannelEntity channel;
  private List<PrivateMessageEntity> messages;

  public PrivateMessageChannelEntity getChannel() {
    return channel;
  }

  public void setChannel(PrivateMessageChannelEntity channel) {
    this.channel = channel;
  }

  public List<PrivateMessageEntity> getMessages() {
    return messages;
  }

  public void setMessages(List<PrivateMessageEntity> messages) {
    this.messages = messages;
  }
}
