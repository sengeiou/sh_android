package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.messages.Message;
import com.shootr.mobile.domain.model.stream.Stream;

public class StreamTimeline implements Message {

  private Stream stream;
  private int participantsNumber;
  private int followingNumber;
  private TimelineItem items;
  private String filter;
  private boolean newBadgeContent;
  private TimelineReposition timelineReposition;
  private long period;
  private TimelineItem polls;
  private TimelineItem highlightedShots;
  private TimelineItem promotedShots;
  private TimelineItem videos;
  private TimelineItem followings;


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

  public TimelineReposition getTimelineReposition() {
    return timelineReposition;
  }

  public void setTimelineReposition(TimelineReposition timelineReposition) {
    this.timelineReposition = timelineReposition;
  }

  public long getPeriod() {
    return period;
  }

  public void setPeriod(long period) {
    this.period = period;
  }

  public TimelineItem getPolls() {
    return polls;
  }

  public void setPolls(TimelineItem polls) {
    this.polls = polls;
  }

  public TimelineItem getHighlightedShots() {
    return highlightedShots;
  }

  public void setHighlightedShots(TimelineItem highlightedShots) {
    this.highlightedShots = highlightedShots;
  }

  public TimelineItem getPromotedShots() {
    return promotedShots;
  }

  public void setPromotedShots(TimelineItem promotedShots) {
    this.promotedShots = promotedShots;
  }

  public TimelineItem getVideos() {
    return videos;
  }

  public void setVideos(TimelineItem videos) {
    this.videos = videos;
  }

  public TimelineItem getFollowings() {
    return followings;
  }

  public void setFollowings(TimelineItem followings) {
    this.followings = followings;
  }
}
