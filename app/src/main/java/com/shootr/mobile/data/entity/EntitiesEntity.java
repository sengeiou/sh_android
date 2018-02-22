package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class EntitiesEntity {

  private ArrayList<UrlEntity> urls;
  private ArrayList<BaseMessagePollEntity> polls;
  private ArrayList<StreamIndexEntity> streams;
  private ArrayList<ImageMediaEntity> images;
  private ArrayList<MentionsEntity> mentions;

  public ArrayList<UrlEntity> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlEntity> urls) {
    this.urls = urls;
  }

  public ArrayList<BaseMessagePollEntity> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<BaseMessagePollEntity> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndexEntity> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndexEntity> streams) {
    this.streams = streams;
  }

  public ArrayList<ImageMediaEntity> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageMediaEntity> images) {
    this.images = images;
  }

  public ArrayList<MentionsEntity> getMentions() {
    return mentions;
  }

  public void setMentions(ArrayList<MentionsEntity> mentions) {
    this.mentions = mentions;
  }

  @Override public String toString() {
    return "EntitiesEntity{"
        + "urls="
        + urls
        + ", polls="
        + polls
        + ", streams="
        + streams
        + ", images="
        + images
        + ", mentions="
        + mentions
        + '}';
  }
}
