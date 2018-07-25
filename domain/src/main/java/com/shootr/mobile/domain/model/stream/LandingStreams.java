package com.shootr.mobile.domain.model.stream;

import com.shootr.mobile.domain.model.HotStreams;
import com.shootr.mobile.domain.model.PromotedItems;
import com.shootr.mobile.domain.model.UserStreams;

public class LandingStreams {

  private UserStreams userStreams;
  private HotStreams hotStreams;
  private PromotedItems promoted;

  public UserStreams getUserStreams() {
    return userStreams;
  }

  public void setUserStreams(UserStreams userStreams) {
    this.userStreams = userStreams;
  }

  public HotStreams getHotStreams() {
    return hotStreams;
  }

  public void setHotStreams(HotStreams hotStreams) {
    this.hotStreams = hotStreams;
  }

  public PromotedItems getPromoted() {
    return promoted;
  }

  public void setPromoted(PromotedItems promoted) {
    this.promoted = promoted;
  }

  @Override public String toString() {
    return "LandingStreams{"
        + "userStreams="
        + userStreams
        + ", hotStreams="
        + hotStreams
        + ", promoted="
        + promoted
        + '}';
  }
}
