package com.shootr.mobile.data.entity;

public class TimelineEntity {

  private StreamEntity stream;
  private ParticipantsEntity participants;
  private ItemsEntity items;
  private String filter;
  private boolean newBadgeContent;
  private ParamsEntity params;
  private ItemsEntity polls;
  private ItemsEntity highlightedShots;
  private ItemsEntity promotedShots;
  private ItemsEntity videos;
  private ItemsEntity followings;

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

  public ParamsEntity getParams() {
    return params;
  }

  public void setParams(ParamsEntity params) {
    this.params = params;
  }

  public ItemsEntity getPolls() {
    return polls;
  }

  public void setPolls(ItemsEntity polls) {
    this.polls = polls;
  }

  public ItemsEntity getHighlightedShots() {
    return highlightedShots;
  }

  public void setHighlightedShots(ItemsEntity highlightedShots) {
    this.highlightedShots = highlightedShots;
  }

  public ItemsEntity getPromotedShots() {
    return promotedShots;
  }

  public void setPromotedShots(ItemsEntity promotedShots) {
    this.promotedShots = promotedShots;
  }

  public ItemsEntity getVideos() {
    return videos;
  }

  public void setVideos(ItemsEntity videos) {
    this.videos = videos;
  }

  public ItemsEntity getFollowings() {
    return followings;
  }

  public void setFollowings(ItemsEntity followings) {
    this.followings = followings;
  }
}
