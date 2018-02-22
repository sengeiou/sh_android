package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.ArrayList;


public class MentionModel implements Serializable {

  private String idUser;
  private String username;
  private ArrayList<Integer> indices;

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ArrayList<Integer> getIndices() {
    return indices;
  }

  public void setIndices(ArrayList<Integer> indices) {
    this.indices = indices;
  }
}
