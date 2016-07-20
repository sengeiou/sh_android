package com.shootr.mobile.domain;

import java.util.List;

public class Poll {

  private String idPoll;
  private String idStream;
  private String idUser;
  private String title;
  private String question;
  private Boolean hasVoted;
  private String status;
  private Boolean published;
  private List<PollOption> pollOptions;
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public Boolean hasVoted() {
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

  public Boolean getPublished() {
    return published;
  }

  public void setPublished(Boolean published) {
    this.published = published;
  }

  public List<PollOption> getPollOptions() {
    return pollOptions;
  }

  public void setPollOptions(List<PollOption> pollOptions) {
    this.pollOptions = pollOptions;
  }

  public String getVoteStatus() {
    return voteStatus;
  }

  public void setVoteStatus(String voteStatus) {
    this.voteStatus = voteStatus;
  }

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }
}
