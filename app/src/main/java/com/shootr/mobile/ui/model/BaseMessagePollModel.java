package com.shootr.mobile.ui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class BaseMessagePollModel implements Serializable {

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
}
