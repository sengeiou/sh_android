package com.shootr.mobile.domain.model.shot;

import com.shootr.mobile.domain.model.TimelineItem;

public class Replies {

  private TimelineItem promoted;
  private TimelineItem subscribers;
  private TimelineItem basic;

  public TimelineItem getPromoted() {
    return promoted;
  }

  public void setPromoted(TimelineItem promoted) {
    this.promoted = promoted;
  }

  public TimelineItem getSubscribers() {
    return subscribers;
  }

  public void setSubscribers(TimelineItem subscribers) {
    this.subscribers = subscribers;
  }

  public TimelineItem getBasic() {
    return basic;
  }

  public void setBasic(TimelineItem basic) {
    this.basic = basic;
  }
}
