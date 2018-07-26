package com.shootr.mobile.util;


public enum  CurrencySymbol {
  EUR("€");

  private String symbol;

  CurrencySymbol(String symbol) {
    this.symbol = symbol;
  }

  public String getSymbol() {
    return symbol;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public static String getSymbol(String currency) {
    CurrencySymbol currencySymbol = valueOf(currency);
    return currencySymbol != null ? currencySymbol.getSymbol() : "";
  }
}
