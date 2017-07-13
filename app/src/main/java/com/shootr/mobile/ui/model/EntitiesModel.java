package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class EntitiesModel implements Serializable {

  private ArrayList<UrlModel> urls;
  private ArrayList<BaseMessagePollModel> polls;
  private ArrayList<StreamIndexModel> streams;

  public ArrayList<UrlModel> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlModel> urls) {
    this.urls = urls;
  }

  public ArrayList<BaseMessagePollModel> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<BaseMessagePollModel> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndexModel> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndexModel> streams) {
    this.streams = streams;
  }
}
