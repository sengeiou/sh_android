package com.shootr.mobile.domain.model.shot;

import java.util.List;

public class ProfileShotTimeline {

  private long maxTimestamp;
  private long sinceTimestamp;
  private List<Shot> shots;

  public long getMaxTimestamp() {
    return maxTimestamp;
  }

  public void setMaxTimestamp(long maxTimestamp) {
    this.maxTimestamp = maxTimestamp;
  }

  public long getSinceTimestamp() {
    return sinceTimestamp;
  }

  public void setSinceTimestamp(long sinceTimestamp) {
    this.sinceTimestamp = sinceTimestamp;
  }

  public List<Shot> getShots() {
    return shots;
  }

  public void setShots(List<Shot> shots) {
    this.shots = shots;
  }
}
