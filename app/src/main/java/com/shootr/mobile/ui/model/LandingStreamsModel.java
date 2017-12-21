package com.shootr.mobile.ui.model;

import java.util.ArrayList;

public class LandingStreamsModel {

  private ArrayList<StreamModel> userStreams;
  private ArrayList<StreamModel> hotStreams;

  public ArrayList<StreamModel> getUserStreams() {
    return userStreams;
  }

  public void setUserStreams(ArrayList<StreamModel> userStreams) {
    this.userStreams = userStreams;
  }

  public ArrayList<StreamModel> getHotStreams() {
    return hotStreams;
  }

  public void setHotStreams(ArrayList<StreamModel> hotStreams) {
    this.hotStreams = hotStreams;
  }
}
