package com.shootr.mobile.data.api.entity;

public class PaginationApiEntity {

  private long maxTimestamp;
  private long sinceTimestamp;

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
}
