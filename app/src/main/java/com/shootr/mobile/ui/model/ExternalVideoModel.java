package com.shootr.mobile.ui.model;

public class ExternalVideoModel implements PrintableModel {

  private String provider;
  private String videoId;
  private String timelineGroup;

  public String getProvider() {
    return provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  public String getVideoId() {
    return videoId;
  }

  public void setVideoId(String videoId) {
    this.videoId = videoId;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ExternalVideoModel that = (ExternalVideoModel) o;

    if (getProvider() != null ? !getProvider().equals(that.getProvider())
        : that.getProvider() != null) {
      return false;
    }
    return getVideoId() != null ? getVideoId().equals(that.getVideoId())
        : that.getVideoId() == null;
  }

  @Override public int hashCode() {
    int result = getProvider() != null ? getProvider().hashCode() : 0;
    result = 31 * result + (getVideoId() != null ? getVideoId().hashCode() : 0);
    return result;
  }

  @Override public String getTimelineGroup() {
    return timelineGroup;
  }

  @Override public void setTimelineGroup(String timelineGroup) {
    this.timelineGroup = timelineGroup;
  }

  @Override public Long getOrder() {
    return 0L;
  }
}
