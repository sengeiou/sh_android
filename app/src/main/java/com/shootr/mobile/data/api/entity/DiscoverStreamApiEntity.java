package com.shootr.mobile.data.api.entity;

import com.google.gson.annotations.SerializedName;
import com.shootr.mobile.data.entity.StreamEntity;
import java.util.ArrayList;

public class DiscoverStreamApiEntity {
  @SerializedName("stream")
  private StreamEntity streamEntity;
  @SerializedName("shots")
  private ArrayList<ShotApiEntity> shotApiEntities;

  public StreamEntity getStreamEntity() {
    return streamEntity;
  }

  public void setStreamEntity(StreamEntity streamEntity) {
    this.streamEntity = streamEntity;
  }

  public ArrayList<ShotApiEntity> getShotApiEntities() {
    return shotApiEntities;
  }

  public void setShotApiEntities(ArrayList<ShotApiEntity> shotApiEntities) {
    this.shotApiEntities = shotApiEntities;
  }
}
