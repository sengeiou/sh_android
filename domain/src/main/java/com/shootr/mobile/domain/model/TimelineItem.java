package com.shootr.mobile.domain.model;

public class TimelineItem extends DataItem {

  private long maxTimestamp;
  private long sinceTimstamp;

  public long getMaxTimestamp() {
    return maxTimestamp;
  }

  public void setMaxTimestamp(long maxTimestamp) {
    this.maxTimestamp = maxTimestamp;
  }

  public long getSinceTimstamp() {
    return sinceTimstamp;
  }

  public void setSinceTimstamp(long sinceTimstamp) {
    this.sinceTimstamp = sinceTimstamp;
  }
}
