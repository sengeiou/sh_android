package com.shootr.mobile.domain.model.shot;

import java.util.ArrayList;

public class Entities {

  private ArrayList<Url> urls;
  private ArrayList<Poll> polls;
  private ArrayList<StreamIndex> streams;

  public ArrayList<Url> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<Url> urls) {
    this.urls = urls;
  }

  public ArrayList<Poll> getPolls() {
    return polls;
  }

  public void setPolls(ArrayList<Poll> polls) {
    this.polls = polls;
  }

  public ArrayList<StreamIndex> getStreams() {
    return streams;
  }

  public void setStreams(ArrayList<StreamIndex> streams) {
    this.streams = streams;
  }
}
