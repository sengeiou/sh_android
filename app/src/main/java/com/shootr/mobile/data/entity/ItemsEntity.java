package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PaginationApiEntity;

public class ItemsEntity extends DataEntity {

  private PaginationApiEntity pagination;

  public PaginationApiEntity getPagination() {
    return pagination;
  }

  public void setPagination(PaginationApiEntity pagination) {
    this.pagination = pagination;
  }
}
