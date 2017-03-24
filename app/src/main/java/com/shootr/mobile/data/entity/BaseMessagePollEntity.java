package com.shootr.mobile.data.entity;

import java.util.ArrayList;

public class BaseMessagePollEntity {

  private String idPoll;
  private String pollQuestion;
  private ArrayList<Integer> indices;

  public String getIdPoll() {
    return idPoll;
  }

  public void setIdPoll(String idPoll) {
    this.idPoll = idPoll;
  }

  public String getPollQuestion() {
    return pollQuestion;
  }

  public void setPollQuestion(String pollQuestion) {
    this.pollQuestion = pollQuestion;
  }

  public ArrayList<Integer> getIndices() {
    return indices;
  }

  public void setIndices(ArrayList<Integer> indices) {
    this.indices = indices;
  }

  @Override public String toString() {
    return "BaseMessagePollEntity{" +
        "idPoll='" + idPoll + '\'' +
        ", pollQuestion='" + pollQuestion + '\'' +
        ", indices=" + indices +
        '}';
  }
}
