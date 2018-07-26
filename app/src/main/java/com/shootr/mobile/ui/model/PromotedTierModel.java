package com.shootr.mobile.ui.model;

import java.util.Comparator;

public class PromotedTierModel {

  private String productId;
  private double price;
  private String currency;
  private BackgroundModel background;
  private BenefitsModel benefits;
  private String displayPrice;

  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public BackgroundModel getBackground() {
    return background;
  }

  public void setBackground(BackgroundModel background) {
    this.background = background;
  }

  public BenefitsModel getBenefits() {
    return benefits;
  }

  public void setBenefits(BenefitsModel benefits) {
    this.benefits = benefits;
  }

  public String getDisplayPrice() {
    return displayPrice;
  }

  public void setDisplayPrice(String displayPrice) {
    this.displayPrice = displayPrice;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PromotedTierModel that = (PromotedTierModel) o;

    return getProductId() != null ? getProductId().equals(that.getProductId())
        : that.getProductId() == null;
  }

  @Override public int hashCode() {
    return getProductId() != null ? getProductId().hashCode() : 0;
  }

  public static class PromotedComparator implements Comparator<PromotedTierModel> {

    @Override public int compare(PromotedTierModel promoted1, PromotedTierModel promoted2) {
      if (promoted1.getPrice() < promoted2.getPrice()) return -1;
      if (promoted1.getPrice() > promoted2.getPrice()) return 1;
      return 0;
    }
  }

}
