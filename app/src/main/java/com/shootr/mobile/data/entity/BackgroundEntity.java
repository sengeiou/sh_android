package com.shootr.mobile.data.entity;

import java.util.List;

public class BackgroundEntity {

  private String type;
  private List<String> colors;
  private int angle;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<String> getColors() {
    return colors;
  }

  public void setColors(List<String> colors) {
    this.colors = colors;
  }

  public int getAngle() {
    return angle;
  }

  public void setAngle(int angle) {
    this.angle = angle;
  }
}
