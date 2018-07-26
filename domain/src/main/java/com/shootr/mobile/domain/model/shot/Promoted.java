package com.shootr.mobile.domain.model.shot;


public class Promoted {

  private double price;
  private double displayPrice;
  private String currency;
  private Background background;

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

  public Background getBackground() {
    return background;
  }

  public void setBackground(Background background) {
    this.background = background;
  }
}
