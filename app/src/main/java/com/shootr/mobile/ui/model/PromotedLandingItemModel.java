package com.shootr.mobile.ui.model;

public class PromotedLandingItemModel {

  private String subtitle;
  private ImageMediaModel imageMediaModel;
  private PrintableModel data;

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public ImageMediaModel getImageMediaModel() {
    return imageMediaModel;
  }

  public void setImageMediaModel(ImageMediaModel imageMediaModel) {
    this.imageMediaModel = imageMediaModel;
  }

  public PrintableModel getData() {
    return data;
  }

  public void setData(PrintableModel data) {
    this.data = data;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PromotedLandingItemModel that = (PromotedLandingItemModel) o;

    if (getSubtitle() != null ? !getSubtitle().equals(that.getSubtitle())
        : that.getSubtitle() != null) {
      return false;
    }
    if (getImageMediaModel() != null ? !getImageMediaModel().equals(that.getImageMediaModel())
        : that.getImageMediaModel() != null) {
      return false;
    }
    return getData() != null ? getData().equals(that.getData()) : that.getData() == null;
  }

  @Override public int hashCode() {
    int result = getSubtitle() != null ? getSubtitle().hashCode() : 0;
    result = 31 * result + (getImageMediaModel() != null ? getImageMediaModel().hashCode() : 0);
    result = 31 * result + (getData() != null ? getData().hashCode() : 0);
    return result;
  }
}
