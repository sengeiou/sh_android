package com.shootr.mobile.data.api.entity;

import java.util.ArrayList;

public class BaseMessageEntitiesApiEntity {

  ArrayList<UrlApiEntity> urls;

  public ArrayList<UrlApiEntity> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlApiEntity> urls) {
    this.urls = urls;
  }
}
