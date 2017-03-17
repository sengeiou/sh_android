package com.shootr.mobile.domain.model.shot;

import java.util.ArrayList;

public class Url {

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
}
