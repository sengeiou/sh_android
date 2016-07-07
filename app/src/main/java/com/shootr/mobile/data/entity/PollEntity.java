package com.shootr.mobile.data.entity;

import java.util.List;

public class PollEntity {

  private String idPoll;
  private String idStream;
  private String question;
  private Long hasVoted;
  private String status;
  private Long published;
  private List<PollOptionEntity> pollOptions;
  private String voteStatus;

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

  public Long getHasVoted() {
    return hasVoted;
  }

  public void setHasVoted(Long hasVoted) {
    this.hasVoted = hasVoted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Long getPublished() {
    return published;
  }

  public void setPublished(Long published) {
    this.published = published;
  }

  public List<PollOptionEntity> getPollOptions() {
    return pollOptions;
  }

  public void setPollOptions(List<PollOptionEntity> pollOptions) {
    this.pollOptions = pollOptions;
  }

  public String getVoteStatus() {
    return voteStatus;
  }

  public void setVoteStatus(String voteStatus) {
    this.voteStatus = voteStatus;
  }
}