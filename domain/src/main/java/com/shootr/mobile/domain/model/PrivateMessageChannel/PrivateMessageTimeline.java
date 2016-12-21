package com.shootr.mobile.domain.model.privateMessageChannel;

import com.shootr.mobile.domain.model.privateMessage.PrivateMessage;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessageTimeline {

  private List<PrivateMessage> privateMessages;

  private PrivateMessageChannel privateMessageChannel;

  public PrivateMessageTimeline() {
    privateMessageChannel = new PrivateMessageChannel();
    privateMessages = new ArrayList<>();
  }

  public List<PrivateMessage> getPrivateMessages() {
    return privateMessages;
  }

  public void setPrivateMessages(List<PrivateMessage> privateMessages) {
    this.privateMessages = privateMessages;
  }

  public PrivateMessageChannel getPrivateMessageChannel() {
    return privateMessageChannel;
  }

  public void setPrivateMessageChannel(PrivateMessageChannel privateMessageChannel) {
    this.privateMessageChannel = privateMessageChannel;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof PrivateMessageTimeline)) return false;

    PrivateMessageTimeline timeline = (PrivateMessageTimeline) o;

    return !(privateMessages != null ? !privateMessages.equals(timeline.privateMessages)
        : timeline.privateMessages != null);
  }

  @Override public int hashCode() {
    return privateMessages != null ? privateMessages.hashCode() : 0;
  }

  @Override public String toString() {
    return "Timeline{" +
        "shots=" + privateMessages +
        '}';
  }
}
