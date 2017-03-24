package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class EntitiesEntity {

  private ArrayList<UrlEntity> urls;
  private ArrayList<BaseMessagePollEntity> polls;

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

  @Override public String toString() {
    return "EntitiesEntity{" +
        "urls=" + urls +
        ", polls=" + polls +
        '}';
  }
}
