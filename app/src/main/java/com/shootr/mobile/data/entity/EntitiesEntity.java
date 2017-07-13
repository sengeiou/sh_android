package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class EntitiesEntity {

  private ArrayList<UrlEntity> urls;
  private ArrayList<BaseMessagePollEntity> polls;
  private ArrayList<StreamIndexEntity> streams;

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

  @Override public String toString() {
    return "EntitiesEntity{" +
        "urls=" + urls +
        ", polls=" + polls +
        ", streams=" + streams +
        '}';
  }
}
