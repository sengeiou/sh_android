package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class EntitiesModel implements Serializable {

  private ArrayList<UrlModel> urls;

  public ArrayList<UrlModel> getUrls() {
    return urls;
  }

  public void setUrls(ArrayList<UrlModel> urls) {
    this.urls = urls;
  }
}
