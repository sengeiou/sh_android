package com.shootr.mobile.ui.model;

import java.util.List;

public class FollowModel {

  private long maxTimestamp;
  private long sinceTimestamp;
  List<SearchableModel> data;

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

  public List<SearchableModel> getData() {
    return data;
  }

  public void setData(List<SearchableModel> data) {
    this.data = data;
  }
}
