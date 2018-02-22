package com.shootr.mobile.data.api.entity;

public class ItemsApiEntity extends DataApiEntity {

  private PaginationApiEntity pagination;

  public PaginationApiEntity getPagination() {
    return pagination;
  }

  public void setPagination(PaginationApiEntity pagination) {
    this.pagination = pagination;
  }
}
