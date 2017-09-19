package com.shootr.mobile.data.entity;

import java.util.List;

public class OnBoardingFavoritesEntity {

  private List<String> idStream;
  private List<String> idUser;

  public List<String> getIdStream() {
    return idStream;
  }

  public void setIdStream(List<String> idStream) {
    this.idStream = idStream;
  }

  public List<String> getIdUser() {
    return idUser;
  }

  public void setIdUser(List<String> idUser) {
    this.idUser = idUser;
  }
}
