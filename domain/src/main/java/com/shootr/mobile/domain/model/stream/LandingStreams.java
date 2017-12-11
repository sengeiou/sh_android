package com.shootr.mobile.domain.model.stream;

import com.shootr.mobile.domain.model.HotStreams;
import com.shootr.mobile.domain.model.UserStreams;

public class LandingStreams {

  private UserStreams userStreams;
  private HotStreams hotStreams;

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
}
