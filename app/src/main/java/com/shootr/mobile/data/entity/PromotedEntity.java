package com.shootr.mobile.data.entity;


public class PromotedEntity {

  private double price;
  private double displayPrice;
  private String currency;
  private BackgroundEntity background;

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

  public BackgroundEntity getBackground() {
    return background;
  }

  public void setBackground(BackgroundEntity background) {
    this.background = background;
  }
}
