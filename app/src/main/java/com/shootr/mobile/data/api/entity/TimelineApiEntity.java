package com.shootr.mobile.data.api.entity;

import com.google.gson.annotations.SerializedName;
import com.shootr.mobile.data.entity.ParticipantsEntity;
import com.shootr.mobile.data.entity.StreamEntity;

public class TimelineApiEntity {

  private StreamEntity stream;
  @SerializedName("connected")
  private ParticipantsEntity participants;
  @SerializedName("header2")
  private DataApiEntity fixed;
  @SerializedName("header1")
  private DataApiEntity pinned;
  private ItemsApiEntity items;
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

  public DataApiEntity getFixed() {
    return fixed;
  }

  public void setFixed(DataApiEntity fixed) {
    this.fixed = fixed;
  }

  public DataApiEntity getPinned() {
    return pinned;
  }

  public void setPinned(DataApiEntity pinned) {
    this.pinned = pinned;
  }

  public ItemsApiEntity getItems() {
    return items;
  }

  public void setItems(ItemsApiEntity items) {
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
