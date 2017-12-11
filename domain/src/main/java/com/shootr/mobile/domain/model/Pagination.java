package com.shootr.mobile.domain.model;

public class Pagination {

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
