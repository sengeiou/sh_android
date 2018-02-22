package com.shootr.mobile.domain.model;

public class TimelineSocketMessage extends SocketMessage {

  public TimelineSocketMessage() {
    setEventType(SocketMessage.TIMELINE);
  }

  private StreamTimeline data;

  public StreamTimeline getData() {
    return data;
  }

  public void setData(StreamTimeline data) {
    this.data = data;
  }
}
