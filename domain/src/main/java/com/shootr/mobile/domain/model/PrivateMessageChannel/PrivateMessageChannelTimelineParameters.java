package com.shootr.mobile.domain.model.privateMessageChannel;

import com.shootr.mobile.domain.model.stream.TimelineParameters;

public class PrivateMessageChannelTimelineParameters extends TimelineParameters {

  private String privateMessageChannelId;

  private String userId;
  private Boolean isRealTime;

  private PrivateMessageChannelTimelineParameters() {
        /* private constructor, use builder */
  }

  public static PrivateMessageChannelTimelineParameters.Builder builder() {
    return new PrivateMessageChannelTimelineParameters.Builder();
  }

  public String privateMessageChannelId() {
    return privateMessageChannelId;
  }

  public String getUserId() {
    return userId;
  }

  public Boolean isRealTime() {
    return isRealTime;
  }

  public void setIsRealTime(Boolean isRealTime) {
    this.isRealTime = isRealTime;
  }

  public static class Builder {

    private PrivateMessageChannelTimelineParameters parameters =
        new PrivateMessageChannelTimelineParameters();

    public Builder() {
      setDefaults();
    }

    private void setDefaults() {
      parameters.limit = DEFAULT_LIMIT;
      parameters.sinceDate = DEFAULT_SINCE_DATE;
    }

    public PrivateMessageChannelTimelineParameters.Builder forChannel(
        PrivateMessageChannel privateMessageChannel) {
      parameters.privateMessageChannelId = privateMessageChannel.getIdPrivateMessageChanel();
      return this;
    }

    public PrivateMessageChannelTimelineParameters.Builder forChannel(
        String idPrivateMessageChannel) {
      parameters.privateMessageChannelId = idPrivateMessageChannel;
      return this;
    }

    public PrivateMessageChannelTimelineParameters.Builder forUser(String idUser) {
      parameters.userId = idUser;
      return this;
    }

    public PrivateMessageChannelTimelineParameters.Builder since(Long sinceDate) {
      parameters.sinceDate = sinceDate;
      return this;
    }

    public PrivateMessageChannelTimelineParameters.Builder maxDate(Long maxDate) {
      parameters.maxDate = maxDate;
      return this;
    }

    public PrivateMessageChannelTimelineParameters.Builder realTime(Boolean isRealTime) {
      parameters.isRealTime = isRealTime;
      return this;
    }

    public PrivateMessageChannelTimelineParameters build() {
      return parameters;
    }
  }
}
