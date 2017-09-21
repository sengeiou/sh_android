package com.shootr.mobile.data.entity;

import java.util.List;

public class OnBoardingFavoritesEntity {

  private List<String> idStreams;
  private List<String> idUsers;

  public List<String> getIdStreams() {
    return idStreams;
  }

  public void setIdStreams(List<String> idStreams) {
    this.idStreams = idStreams;
  }

  public List<String> getIdUsers() {
    return idUsers;
  }

  public void setIdUsers(List<String> idUsers) {
    this.idUsers = idUsers;
  }
}
