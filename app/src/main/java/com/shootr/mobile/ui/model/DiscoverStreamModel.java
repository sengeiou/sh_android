package com.shootr.mobile.ui.model;

import java.util.ArrayList;

public class DiscoverStreamModel {

  private StreamModel streamModel;
  private ArrayList<ShotModel> shotModels;

  public StreamModel getStreamModel() {
    return streamModel;
  }

  public void setStreamModel(StreamModel streamModel) {
    this.streamModel = streamModel;
  }

  public ArrayList<ShotModel> getShotModels() {
    return shotModels;
  }

  public void setShotModels(ArrayList<ShotModel> shotModels) {
    this.shotModels = shotModels;
  }
}
