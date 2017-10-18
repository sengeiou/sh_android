package com.shootr.mobile.domain.model.poll;

import com.shootr.mobile.domain.model.ImageMedia;
import java.util.Comparator;

public class PollOption {

  private String idPoll;
  private String idPollOption;
  private ImageMedia optionImage;
  private String imageUrl;
  private String title;
  private Long votes;
  private Integer order;

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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Long getVotes() {
    return votes;
  }

  public void setVotes(Long votes) {
    this.votes = votes;
  }

  public Integer getOrder() {
    return order;
  }

  public void setOrder(Integer order) {
    this.order = order;
  }

  public static Comparator<PollOption> PollOptionComparator
      = new Comparator<PollOption>() {

    public int compare(PollOption pollOption, PollOption anotherPollOption) {
      return pollOption.getOrder().compareTo(anotherPollOption.getOrder());
    }

  };

  public static Comparator<PollOption> PollOptionVotesComparator
      = new Comparator<PollOption>() {

    public int compare(PollOption pollOption, PollOption anotherPollOption) {
      return anotherPollOption.getVotes().compareTo(pollOption.getVotes());
    }

  };

  public ImageMedia getOptionImage() {
    return optionImage;
  }

  public void setOptionImage(ImageMedia optionImage) {
    this.optionImage = optionImage;
  }
}
