package com.shootr.mobile.data.api.entity;

import java.util.ArrayList;

public class BaseMessageEntitiesApiEntity {

  private ArrayList<UrlApiEntity> urls;
  private ArrayList<BaseMessagePollApiEntity> polls;
  private ArrayList<StreamIndexApiEntity> streams;

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
}
