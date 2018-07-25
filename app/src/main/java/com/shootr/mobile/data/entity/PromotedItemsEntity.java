package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class PromotedItemsEntity {

  private ArrayList<PromotedLandingItemEntity> data;

  public ArrayList<PromotedLandingItemEntity> getPromoted() {
    return data;
  }

  public void setPromoted(ArrayList<PromotedLandingItemEntity> promoted) {
    this.data = promoted;
  }
}
