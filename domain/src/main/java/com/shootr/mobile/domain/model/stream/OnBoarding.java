package com.shootr.mobile.domain.model.stream;

import com.shootr.mobile.domain.model.user.User;

public class OnBoarding {

  private Stream stream;
  private User user;
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

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
