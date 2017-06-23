package com.shootr.mobile.data.entity;

import java.util.List;

public class PollEntity {

  private String idPoll;
  private String idStream;
  private String idUser;
  private String question;
  private Boolean userHasVoted;
  private String status;
  private Long published;
  private List<PollOptionEntity> pollOptions;
  private String voteStatus;
  private String votePrivacy;
  private Long expirationDate;
  private Boolean verifiedPoll;

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

  public Boolean getUserHasVoted() {
    return userHasVoted;
  }

  public void setUserHasVoted(Boolean userHasVoted) {
    this.userHasVoted = userHasVoted;
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

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getVotePrivacy() {
    return votePrivacy;
  }

  public void setVotePrivacy(String votePrivacy) {
    this.votePrivacy = votePrivacy;
  }

  public Long getExpirationDate() {
    return expirationDate;
  }

  public void setExpirationDate(Long expirationDate) {
    this.expirationDate = expirationDate;
  }

  public Boolean getVerifiedPoll() {
    return verifiedPoll;
  }

  public void setVerifiedPoll(Boolean verifiedPoll) {
    this.verifiedPoll = verifiedPoll;
  }
}
