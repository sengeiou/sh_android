package com.shootr.mobile.ui.model;

import com.shootr.mobile.domain.model.poll.PollStatus;
import java.io.Serializable;
import java.util.List;

public class PollModel implements PrintableModel, Serializable {

  private final long ONE_HOUR_MILISECONDS = 3600000;
  private String idPoll;
  private String idStream;
  private String idUser;
  private String question;
  private String status;
  private String voteStatus;
  private Boolean published;
  private List<PollOptionModel> pollOptionModels;
  private String streamTitle;
  private String votePrivacy;
  private Long expirationDate;
  private boolean verifiedPoll;
  private boolean hideResults;
  private boolean canVote;
  private boolean dailyPoll;
  private String timelineGroup;

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
    if (status.equals(PollStatus.CLOSED)) {
      return true;
    } else if (this.expirationDate != null) {
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

  public boolean isHideResults() {
    return hideResults;
  }

  public void setHideResults(boolean hideResults) {
    this.hideResults = hideResults;
  }

  public boolean canVote() {
    return canVote;
  }

  public void setCanVote(boolean canVote) {
    this.canVote = canVote;
  }

  public boolean isDailyPoll() {
    return dailyPoll;
  }

  public void setDailyPoll(boolean dailyPoll) {
    this.dailyPoll = dailyPoll;
  }

  @Override public String getTimelineGroup() {
    return timelineGroup;
  }

  @Override public void setTimelineGroup(String timelineGroup) {
    this.timelineGroup = timelineGroup;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PollModel pollModel = (PollModel) o;

    if (canVote != pollModel.canVote) return false;
    if (!getIdPoll().equals(pollModel.getIdPoll())) return false;
    if (getIdStream() != null ? !getIdStream().equals(pollModel.getIdStream())
        : pollModel.getIdStream() != null) {
      return false;
    }
    return getStatus() != null ? getStatus().equals(pollModel.getStatus())
        : pollModel.getStatus() == null;
  }

  @Override public int hashCode() {
    int result = getIdPoll().hashCode();
    result = 31 * result + (getIdStream() != null ? getIdStream().hashCode() : 0);
    result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
    result = 31 * result + (canVote ? 1 : 0);
    return result;
  }
}
