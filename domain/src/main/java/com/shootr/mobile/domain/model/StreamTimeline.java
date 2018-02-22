package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.messages.Message;
import com.shootr.mobile.domain.model.stream.Stream;

public class StreamTimeline implements Message {

  private Stream stream;
  private int participantsNumber;
  private int followingNumber;
  private DataItem fixed;
  private DataItem pinned;
  private TimelineItem items;
  private String filter;
  private boolean newBadgeContent;


  public Stream getStream() {
    return stream;
  }

  public void setStream(Stream stream) {
    this.stream = stream;
  }

  public int getParticipantsNumber() {
    return participantsNumber;
  }

  public void setParticipantsNumber(int participantsNumber) {
    this.participantsNumber = participantsNumber;
  }

  public int getFollowingNumber() {
    return followingNumber;
  }

  public void setFollowingNumber(int followingNumber) {
    this.followingNumber = followingNumber;
  }

  public DataItem getFixed() {
    return fixed;
  }

  public void setFixed(DataItem fixed) {
    this.fixed = fixed;
  }

  public DataItem getPinned() {
    return pinned;
  }

  public void setPinned(DataItem pinned) {
    this.pinned = pinned;
  }

  public TimelineItem getItems() {
    return items;
  }

  public void setItems(TimelineItem items) {
    this.items = items;
  }

  @Override public String getMessageType() {
    return Message.TIMELINE;
  }

  public String getFilter() {
    return filter;
  }

  public void setFilter(String filter) {
    this.filter = filter;
  }

  public boolean isNewBadgeContent() {
    return newBadgeContent;
  }

  public void setNewBadgeContent(boolean newBadgeContent) {
    this.newBadgeContent = newBadgeContent;
  }

}
