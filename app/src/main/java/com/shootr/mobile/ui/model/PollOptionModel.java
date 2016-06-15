package com.shootr.mobile.ui.model;

public class PollOptionModel {

  private String idPoll;
  private String idPollOption;
  private String imageUrl;
  private String text;
  private Long votes;

  public String getIdPoll() {
    return idPoll;
  }

  public void setIdPoll(String idPoll) {
    this.idPoll = idPoll;
  }

  public String getIdPollOption() {
    return idPollOption;
  }

  public void setIdPollOption(String idPollOption) {
    this.idPollOption = idPollOption;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Long getVotes() {
    return votes;
  }

  public void setVotes(Long votes) {
    this.votes = votes;
  }
}
