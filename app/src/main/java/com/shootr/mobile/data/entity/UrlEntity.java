package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class UrlEntity {

  private String url;
  private String displayUrl;
  private ArrayList<Integer> indices;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getDisplayUrl() {
    return displayUrl;
  }

  public void setDisplayUrl(String displayUrl) {
    this.displayUrl = displayUrl;
  }

  public ArrayList<Integer> getIndices() {
    return indices;
  }

  public void setIndices(ArrayList<Integer> indices) {
    this.indices = indices;
  }

  @Override public String toString() {
    return "UrlEntity{" +
        "url='" + url + '\'' +
        ", displayUrl='" + displayUrl + '\'' +
        ", indices=" + indices +
        '}';
  }
}
