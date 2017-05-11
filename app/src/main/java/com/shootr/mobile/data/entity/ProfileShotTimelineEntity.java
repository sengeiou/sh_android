package com.shootr.mobile.data.entity;

import java.util.List;

public class ProfileShotTimelineEntity {

  private long maxTimestamp;
  private long sinceTimestamp;
  private List<ShotEntity> shotEntities;

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

  public List<ShotEntity> getShotEntities() {
    return shotEntities;
  }

  public void setShotEntities(List<ShotEntity> shotEntities) {
    this.shotEntities = shotEntities;
  }
}
