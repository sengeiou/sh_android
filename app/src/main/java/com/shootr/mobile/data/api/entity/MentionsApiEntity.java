package com.shootr.mobile.data.api.entity;

import java.util.ArrayList;

public class MentionsApiEntity {

  private String idUser;
  private String userName;
  private ArrayList<Integer> indices;

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public ArrayList<Integer> getIndices() {
    return indices;
  }

  public void setIndices(ArrayList<Integer> indices) {
    this.indices = indices;
  }
}
