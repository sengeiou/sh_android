package com.shootr.mobile.domain.model.stream;

public class OnBoardingStream {

  private Stream stream;
  private boolean defaultValue;

  public Stream getStream() {
    return stream;
  }

  public void setStream(Stream stream) {
    this.stream = stream;
  }

  public boolean isFavorite() {
    return defaultValue;
  }

  public void setDefaultValue(boolean defaultValue) {
    this.defaultValue = defaultValue;
  }
}
