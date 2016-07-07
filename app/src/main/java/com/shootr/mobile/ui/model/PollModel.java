package com.shootr.mobile.ui.model;

import java.util.List;

public class PollModel {

  private String idPoll;
  private String idStream;
  private String question;
  private Boolean hasVoted;
  private String status;
  private String voteStatus;
  private Boolean published;
  private List<PollOptionModel> pollOptionModels;

  public String getIdPoll() {
    return idPoll;
  }

  public void setIdPoll(String idPoll) {
    this.idPoll = idPoll;
  }

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public Boolean getHasVoted() {
    return hasVoted;
  }

  public void setHasVoted(Boolean hasVoted) {
    this.hasVoted = hasVoted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getVoteStatus() {
    return voteStatus;
  }

  public void setVoteStatus(String voteStatus) {
    this.voteStatus = voteStatus;
  }

  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public List<PollOptionModel> getPollOptionModels() {
    return pollOptionModels;
  }

  public void setPollOptionModels(List<PollOptionModel> pollOptionModels) {
    this.pollOptionModels = pollOptionModels;
  }
}