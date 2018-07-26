package com.shootr.mobile.ui.model;

public class PromotedModel {

  private double price;
  private double displayPrice;
  private String currency;
  private BackgroundModel background;

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

  public BackgroundModel getBackground() {
    return background;
  }

  public void setBackground(BackgroundModel background) {
    this.background = background;
  }
}
