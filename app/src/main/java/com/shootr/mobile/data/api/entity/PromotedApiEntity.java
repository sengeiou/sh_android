package com.shootr.mobile.data.api.entity;

public class PromotedApiEntity {

  private double price;
  private double displayPrice;
  private String currency;
  private BackgroundApiEntity background;

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public double getDisplayPrice() {
    return displayPrice;
  }

  public void setDisplayPrice(double displayPrice) {
    this.displayPrice = displayPrice;
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
}
