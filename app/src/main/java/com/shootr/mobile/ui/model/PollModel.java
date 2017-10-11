package com.shootr.mobile.ui.model;

import java.util.List;

public class PollModel {

  private final long ONE_HOUR_MILISECONDS = 3600000;
  private String idPoll;
  private String idStream;
  private String idUser;
  private String question;
  private Boolean hasVoted;
  private String status;
  private String voteStatus;
  private Boolean published;
  private List<PollOptionModel> pollOptionModels;
  private String streamTitle;
  private String votePrivacy;
  private Long expirationDate;
  private boolean verifiedPoll;
  private String idPollOptionVoted;

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

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getStreamTitle() {
    return streamTitle;
  }

  public void setStreamTitle(String streamTitle) {
    this.streamTitle = streamTitle;
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

  public boolean isExpired() {
    if (this.expirationDate != null) {
      return (this.expirationDate - System.currentTimeMillis() < 0) ? true : false;
    } else {
      return false;
    }
  }

  public boolean isLessThanHourToExpire() {
    if (this.expirationDate != null) {
      return (this.expirationDate - System.currentTimeMillis() < ONE_HOUR_MILISECONDS) ? true
          : false;
    } else {
      return false;
    }
  }

  public boolean isVerifiedPoll() {
    return verifiedPoll;
  }

  public void setVerifiedPoll(boolean verifiedPoll) {
    this.verifiedPoll = verifiedPoll;
  }

  public String getIdPollOptionVoted() {
    return idPollOptionVoted;
  }

  public void setIdPollOptionVoted(String idPollOptionVoted) {
    this.idPollOptionVoted = idPollOptionVoted;
  }
}
