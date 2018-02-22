package com.shootr.mobile.data.entity;

public class PaginationEntity {

  private Long maxTimestamp;
  private Long sinceTimestamp;

  public Long getMaxTimestamp() {
    return maxTimestamp;
  }

  public void setMaxTimestamp(Long maxTimestamp) {
    this.maxTimestamp = maxTimestamp;
  }

  public Long getSinceTimestamp() {
    return sinceTimestamp;
  }

  public void setSinceTimestamp(Long sinceTimestamp) {
    this.sinceTimestamp = sinceTimestamp;
  }
}
