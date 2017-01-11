package com.shootr.mobile.domain.bus;

public interface ChannelsBadgeChanged {

  interface Receiver {
    void onBadgeChanged(Event event);
  }

  class Event {
    int unreadChannels;
    int unreadFollowChannels;

    public Event() {
      this.unreadChannels = 0;
    }

    public Event(int unreadChannels, int unreadFollowChannels) {
      this.unreadChannels = unreadChannels;
      this.unreadFollowChannels = unreadFollowChannels;
    }

    public int getUnreadChannels() {
      return unreadChannels;
    }

    public void setUnreadChannels(int unreadChannels) {
      this.unreadChannels = unreadChannels;
    }

    public int getUnreadFollowChannels() {
      return unreadFollowChannels;
    }

    public void setUnreadFollowChannels(int unreadFollowChannels) {
      this.unreadFollowChannels = unreadFollowChannels;
    }
  }
}
