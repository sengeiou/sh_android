package com.shootr.mobile.ui.model;

import java.util.ArrayList;

public class PromotedGroupModel {

  private ArrayList<PromotedLandingItemModel> promotedModels;

  public ArrayList<PromotedLandingItemModel> getPromotedModels() {
    return promotedModels;
  }

  public void setPromotedModels(ArrayList<PromotedLandingItemModel> promotedModels) {
    this.promotedModels = promotedModels;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PromotedGroupModel that = (PromotedGroupModel) o;

    return getPromotedModels() != null ? getPromotedModels().equals(that.getPromotedModels())
        : that.getPromotedModels() == null;
  }

  @Override public int hashCode() {
    return getPromotedModels() != null ? getPromotedModels().hashCode() : 0;
  }
}
