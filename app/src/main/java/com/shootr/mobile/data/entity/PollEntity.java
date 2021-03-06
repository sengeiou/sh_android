package com.shootr.mobile.data.entity;

import com.shootr.mobile.data.api.entity.PrintableItemApiEntity;
import com.shootr.mobile.domain.model.PrintableType;
import java.util.Date;
import java.util.List;

public class PollEntity implements PrintableItemApiEntity, PrintableItemEntity, SeenableEntity {

  private String resultType;
  private String idPoll;
  private String idStream;
  private String idUser;
  private String question;
  private String status;
  private Long published;
  private List<PollOptionEntity> pollOptions;
  private String voteStatus;
  private String votePrivacy;
  private Long expirationDate;
  private Boolean verifiedPoll;
  private boolean hideResults;
  private boolean canVote;
  private boolean dailyPoll;
  private String shareLink;
  private Date deleted;
  private Boolean seen;

  public PollEntity() {
    setResultType(PrintableType.POLL);
  }

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

  public Boolean isHideResults() {
    return hideResults;
  }

  public void setHideResults(Boolean hideResults) {
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

  public String getShareLink() {
    return shareLink;
  }

  public void setShareLink(String shareLink) {
    this.shareLink = shareLink;
  }

  public Date getDeleted() {
    return deleted;
  }

  public void setDeleted(Date deleted) {
    this.deleted = deleted;
  }

  @Override public Boolean getSeen() {
    return seen;
  }

  @Override public void setSeen(Boolean seen) {
    this.seen = seen;
  }

  @Override public String getResultType() {
    return resultType;
  }

  @Override public void setResultType(String resultType) {
    this.resultType = resultType;
  }
}
