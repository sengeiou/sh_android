package com.shootr.mobile.data.api.entity;

import com.shootr.mobile.data.api.BenefitsApiEntity;

public class PromotedTierApiEntity {

  private String productId;
  private double price;
  private String currency;
  private BackgroundApiEntity background;
  private BenefitsApiEntity benefits;

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

  public BackgroundApiEntity getBackground() {
    return background;
  }

  public void setBackground(BackgroundApiEntity background) {
    this.background = background;
  }

  public BenefitsApiEntity getBenefits() {
    return benefits;
  }

  public void setBenefits(BenefitsApiEntity benefits) {
    this.benefits = benefits;
  }
}
