package com.shootr.mobile.data.api;

public class BenefitsApiEntity {

  private long length;
  private long duration;
  private boolean important;

  public long getLenght() {
    return length;
  }

  public void setLenght(long lenght) {
    this.length = lenght;
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
