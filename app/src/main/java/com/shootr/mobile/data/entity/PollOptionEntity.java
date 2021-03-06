package com.shootr.mobile.data.entity;

public class PollOptionEntity {

  private String idPoll;
  private String idPollOption;
  private ImageMediaEntity optionImage;
  private String text;
  private Long votes;
  private Integer order;
  private boolean voted;

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

  public Long getVotes() {
    return votes;
  }

  public void setVotes(Long votes) {
    this.votes = votes;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public ImageMediaEntity getOptionImage() {
    return optionImage;
  }

  public void setOptionImage(ImageMediaEntity optionImage) {
    this.optionImage = optionImage;
  }

  public boolean isVoted() {
    return voted;
  }

  public void setVoted(boolean voted) {
    this.voted = voted;
  }
}
