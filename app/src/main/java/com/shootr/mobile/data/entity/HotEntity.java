package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class HotEntity {

  private PaginationEntity pagination;
  private ArrayList<StreamEntity> data;

  public PaginationEntity getPagination() {
    return pagination;
  }

  public void setPagination(PaginationEntity pagination) {
    this.pagination = pagination;
  }

  public ArrayList<StreamEntity> getData() {
    return data;
  }

  public void setData(ArrayList<StreamEntity> data) {
    this.data = data;
  }
}
