package com.shootr.mobile.ui.model;

import java.io.Serializable;

public class PollOptionModel implements Serializable {

  private String idPoll;
  private String idPollOption;
  private ImageMediaModel optionImage;
  private String text;
  private Long votes;
  private boolean isVoted;

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

  public boolean isVoted() {
    return isVoted;
  }

  public void setVoted(boolean voted) {
    isVoted = voted;
  }

  public ImageMediaModel getOptionImage() {
    return optionImage;
  }

  public void setOptionImage(ImageMediaModel optionImage) {
    this.optionImage = optionImage;
  }
}
