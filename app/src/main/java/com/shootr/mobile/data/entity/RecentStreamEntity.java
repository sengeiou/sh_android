package com.shootr.mobile.data.entity;

public class RecentStreamEntity {

  private Long joinStreamDate;
  private StreamEntity stream;

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public Long getJoinStreamDate() {
    return joinStreamDate;
  }

  public void setJoinStreamDate(Long joinStreamDate) {
    this.joinStreamDate = joinStreamDate;
  }
}
