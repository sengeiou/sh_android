package com.shootr.mobile.ui.model;

import java.util.List;

public class ProfileShotTimelineModel {

  private long maxTimestamp;
  private long sinceTimestamp;
  private List<ShotModel> shots;

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

  public List<ShotModel> getShots() {
    return shots;
  }

  public void setShots(List<ShotModel> shots) {
    this.shots = shots;
  }
}
