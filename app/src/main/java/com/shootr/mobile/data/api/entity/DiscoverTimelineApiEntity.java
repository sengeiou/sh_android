package com.shootr.mobile.data.api.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class DiscoverTimelineApiEntity {
  @SerializedName("discoverStreams")
  private ArrayList<DiscoverStreamApiEntity> discoverStreamApiEntities;

  public ArrayList<DiscoverStreamApiEntity> getDiscoverStreamApiEntities() {
    return discoverStreamApiEntities;
  }

  public void setDiscoverStreamApiEntities(
      ArrayList<DiscoverStreamApiEntity> discoverStreamApiEntities) {
    this.discoverStreamApiEntities = discoverStreamApiEntities;
  }
}
