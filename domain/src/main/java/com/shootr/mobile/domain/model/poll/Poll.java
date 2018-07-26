package com.shootr.mobile.domain.model.poll;

import com.shootr.mobile.domain.model.PrintableItem;
import com.shootr.mobile.domain.model.PrintableType;
import com.shootr.mobile.domain.model.Seenable;
import java.util.Date;
import java.util.List;

public class Poll implements PrintableItem, Seenable {

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
  private String votePrivacy;
  private Long expirationDate;
  private boolean verifiedPoll;
  private boolean hideResults;
  private boolean canVote;
  private boolean dailyPoll;
  private String shareLink;
  private Date deleted;
  private Boolean seen;

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

  public String getShareLink() {
    return shareLink;
  }

  public void setShareLink(String shareLink) {
    this.shareLink = shareLink;
  }

  public void setDailyPoll(boolean dailyPoll) {
    this.dailyPoll = dailyPoll;
  }

  @Override public String getResultType() {
    return PrintableType.POLL;
  }

  @Override public Long getOrder() {
    return 0L;
  }

  @Override public Date getDeletedData() {
    return deleted;
  }

  @Override public void setDeletedData(Date deleted) {
    this.deleted = deleted;
  }

  @Override public String getMessageType() {
    return PrintableType.POLL;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Poll poll = (Poll) o;

    return getIdPoll() != null ? getIdPoll().equals(poll.getIdPoll()) : poll.getIdPoll() == null;
  }

  @Override public int hashCode() {
    return getIdPoll() != null ? getIdPoll().hashCode() : 0;
  }

  @Override public Boolean getSeen() {
    return seen;
  }

  @Override public void setSeen(Boolean seen) {
    this.seen = seen;
  }
}
