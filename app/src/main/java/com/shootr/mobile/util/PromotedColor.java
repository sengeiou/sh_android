package com.shootr.mobile.util;


public enum PromotedColor {
  YELLOW_SUBMARINE("#FFCA28", "#000000"),
  LIGHT_ORANGE("#FFA000", "#000000"),
  ORANGE("#FF6F00", "#000000"),
  DARK_ORANGE("#F4511E"),
  BITCHY_RED("#D2341F"),
  DARK_RED("#980000"),
  LIGHT_GREEN("#AFB42B"),
  DARK_GREEN("#388E3C"),
  LIGHT_BLUE("#3D5AFE"),
  BLUE_VALENTINE("#311B92"),
  LIGHT_MAGENTA("#EC407A"),
  DARK_MAGENTA("#AD1457"),
  LIGHT_PURPLE("#AB47BC"),
  DEEP_PURPLE("#6A1B9A");

  private String color;
  private String detailColors;

  PromotedColor(String color, String detailColors) {
    this.color = color;
    this.detailColors = detailColors;
  }

  PromotedColor(String color) {
    this.color = color;
    this.detailColors = "#fafafa";
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getDetailColors() {
    return detailColors;
  }

  public void setDetailColors(String detailColors) {
    this.detailColors = detailColors;
  }
}
