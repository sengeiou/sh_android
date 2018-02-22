package com.shootr.mobile.data.entity;

public class TimelineEntity {

  private StreamEntity stream;
  private ParticipantsEntity participants;
  private DataEntity fixed;
  private DataEntity pinned;
  private ItemsEntity items;
  private String filter;
  private boolean newBadgeContent;

  public StreamEntity getStream() {
    return stream;
  }

  public void setStream(StreamEntity stream) {
    this.stream = stream;
  }

  public ParticipantsEntity getParticipants() {
    return participants;
  }

  public void setParticipants(ParticipantsEntity participants) {
    this.participants = participants;
  }

  public DataEntity getFixed() {
    return fixed;
  }

  public void setFixed(DataEntity fixed) {
    this.fixed = fixed;
  }

  public DataEntity getPinned() {
    return pinned;
  }

  public void setPinned(DataEntity pinned) {
    this.pinned = pinned;
  }

  public ItemsEntity getItems() {
    return items;
  }

  public void setItems(ItemsEntity items) {
    this.items = items;
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
