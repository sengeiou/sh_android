package com.shootr.mobile.domain.model;

import com.shootr.mobile.domain.model.shot.Background;

public class PromotedTier {

  private String productId;
  private double price;
  private String currency;
  private Background background;
  private Benefits benefits;

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

  public Background getBackground() {
    return background;
  }

  public void setBackground(Background background) {
    this.background = background;
  }

  public Benefits getBenefits() {
    return benefits;
  }

  public void setBenefits(Benefits benefits) {
    this.benefits = benefits;
  }
}
