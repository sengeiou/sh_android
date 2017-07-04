package com.shootr.mobile.domain.model;

import java.util.List;

public class Following {

  private long maxTimestamp;
  private long sinceTimestamp;
  List<Followable> data;

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

  public List<Followable> getData() {
    return data;
  }

  public void setData(List<Followable> data) {
    this.data = data;
  }
}
