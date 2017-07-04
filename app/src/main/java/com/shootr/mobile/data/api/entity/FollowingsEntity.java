package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.FollowableEntity;
import java.util.List;

public class FollowingsEntity {

  PaginationApiEntity paginationApiEntity;
  List<FollowableEntity> data;

  public PaginationApiEntity getPaginationApiEntity() {
    return paginationApiEntity;
  }

  public void setPaginationApiEntity(PaginationApiEntity paginationApiEntity) {
    this.paginationApiEntity = paginationApiEntity;
  }

  public List<FollowableEntity> getData() {
    return data;
  }

  public void setData(List<FollowableEntity> data) {
    this.data = data;
  }
}
