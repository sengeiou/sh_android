package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class EntitiesEntity {

  private ArrayList<UrlEntity> urls;

  public ArrayList<UrlEntity> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlEntity> urls) {
    this.urls = urls;
  }

  @Override public String toString() {
    return "EntitiesEntity{" +
        "urls=" + urls +
        '}';
  }
}
