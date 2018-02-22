package com.shootr.mobile.data.api.entity;

import java.util.ArrayList;

public class BaseMessageEntitiesApiEntity {

  private ArrayList<UrlApiEntity> urls;
  private ArrayList<BaseMessagePollApiEntity> polls;
  private ArrayList<StreamIndexApiEntity> streams;
  private ArrayList<ImageMediaApiEntity> images;
  private ArrayList<MentionsApiEntity> mentions;

  public ArrayList<UrlApiEntity> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlApiEntity> urls) {
    this.urls = urls;
  }

  public ArrayList<BaseMessagePollApiEntity> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<BaseMessagePollApiEntity> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndexApiEntity> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndexApiEntity> streams) {
    this.streams = streams;
  }

  public ArrayList<ImageMediaApiEntity> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageMediaApiEntity> images) {
    this.images = images;
  }

  public ArrayList<MentionsApiEntity> getMentions() {
    return mentions;
  }

  public void setMentions(ArrayList<MentionsApiEntity> mentions) {
    this.mentions = mentions;
  }
}
