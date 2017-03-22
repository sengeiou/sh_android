package com.shootr.mobile.domain.model.shot;

import java.util.ArrayList;

public class Entities {

  private ArrayList<Url> urls;
  private ArrayList<Poll> polls;

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
}
