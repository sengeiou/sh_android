package com.shootr.mobile.data.api.entity;

import com.google.gson.annotations.SerializedName;
import com.shootr.mobile.data.entity.ParamsEntity;
import com.shootr.mobile.data.entity.ParticipantsEntity;
import com.shootr.mobile.data.entity.StreamEntity;

public class TimelineApiEntity {

  private StreamEntity stream;
  @SerializedName("connected")
  private ParticipantsEntity participants;
  private ItemsApiEntity polls;
  private ItemsApiEntity highlightedShots;
  private ItemsApiEntity promotedShots;
  private ItemsApiEntity videos;
  private ItemsApiEntity items;
  private ItemsApiEntity followings;
  private String filter;
  private boolean newBadgeContent;
  private ParamsEntity params;


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

  public ParamsEntity getParams() {
    return params;
  }

  public void setParams(ParamsEntity params) {
    this.params = params;
  }

  public ItemsApiEntity getPolls() {
    return polls;
  }

  public void setPolls(ItemsApiEntity polls) {
    this.polls = polls;
  }

  public ItemsApiEntity getHighlightedShots() {
    return highlightedShots;
  }

  public void setHighlightedShots(ItemsApiEntity highlightedShots) {
    this.highlightedShots = highlightedShots;
  }

  public ItemsApiEntity getPromotedShots() {
    return promotedShots;
  }

  public void setPromotedShots(ItemsApiEntity promotedShots) {
    this.promotedShots = promotedShots;
  }

  public ItemsApiEntity getVideos() {
    return videos;
  }

  public void setVideos(ItemsApiEntity videos) {
    this.videos = videos;
  }

  public ItemsApiEntity getFollowings() {
    return followings;
  }

  public void setFollowings(ItemsApiEntity followings) {
    this.followings = followings;
  }
}
