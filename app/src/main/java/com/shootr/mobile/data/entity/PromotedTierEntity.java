package com.shootr.mobile.data.entity;

public class PromotedTierEntity {

  private String productId;
  private double price;
  private String currency;
  private BackgroundEntity background;
  private BenefitsEntity benefits;

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

  public BackgroundEntity getBackground() {
    return background;
  }

  public void setBackground(BackgroundEntity background) {
    this.background = background;
  }

  public BenefitsEntity getBenefits() {
    return benefits;
  }

  public void setBenefits(BenefitsEntity benefits) {
    this.benefits = benefits;
  }
}
