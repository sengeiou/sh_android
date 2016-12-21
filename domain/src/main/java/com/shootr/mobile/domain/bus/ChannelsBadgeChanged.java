package com.shootr.mobile.domain.bus;

public interface ChannelsBadgeChanged {

  interface Receiver {

    void onBadgeChanged(Event event);
  }

  class Event {
    int unreadChannels;

    public Event(int unreadChannels) {
      this.unreadChannels = unreadChannels;
    }

    public int getUnreadChannels() {
      return unreadChannels;
    }

    public void setUnreadChannels(int unreadChannels) {
      this.unreadChannels = unreadChannels;
    }
  }
}
