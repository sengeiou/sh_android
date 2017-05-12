package com.shootr.mobile.data.api.entity;

import java.util.List;

public class ProfileShotTimelineApiEntity {

  private PaginationApiEntity pagination;
  private List<ShotApiEntity> shots;

  public PaginationApiEntity getPagination() {
    return pagination;
  }

  public void setPagination(PaginationApiEntity pagination) {
    this.pagination = pagination;
  }

  public List<ShotApiEntity> getShots() {
    return shots;
  }

  public void setShots(List<ShotApiEntity> shots) {
    this.shots = shots;
  }
}
