package com.shootr.mobile.ui.model;

public class BenefitsModel {

  private long lenght;
  private long duration;
  private boolean important;

  public long getLenght() {
    return lenght;
  }

  public void setLenght(long lenght) {
    this.lenght = lenght;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public boolean isImportant() {
    return important;
  }

  public void setImportant(boolean important) {
    this.important = important;
  }
}
