package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class DiscoverStreamEntity {

  private StreamEntity streamEntity;
  private ArrayList<ShotEntity> shotEntities;

  public StreamEntity getStreamEntity() {
    return streamEntity;
  }

  public void setStreamEntity(StreamEntity streamEntity) {
    this.streamEntity = streamEntity;
  }

  public ArrayList<ShotEntity> getShotEntities() {
    return shotEntities;
  }

  public void setShotEntities(ArrayList<ShotEntity> shotEntities) {
    this.shotEntities = shotEntities;
  }
}
