package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.entity.FollowableEntity;
import java.util.List;

public class FollowingsEntity {

  PaginationApiEntity pagination;
  List<FollowableEntity> data;

  public PaginationApiEntity getPagination() {
    return pagination;
  }

  public void setPagination(PaginationApiEntity pagination) {
    this.pagination = pagination;
  }

  public List<FollowableEntity> getData() {
    return data;
  }

  public void setData(List<FollowableEntity> data) {
    this.data = data;
  }
}
