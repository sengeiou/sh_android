package com.shootr.mobile.data.entity;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;

public class MentionsEntity {

  private String idUser;
  @SerializedName("userName")
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

  @Override public String toString() {
    return "MentionsEntity{"
        + "idUser='"
        + idUser
        + '\''
        + ", username='"
        + username
        + '\''
        + ", indices="
        + indices
        + '}';
  }
}
