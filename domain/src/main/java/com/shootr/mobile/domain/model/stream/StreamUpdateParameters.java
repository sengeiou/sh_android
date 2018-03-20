package com.shootr.mobile.domain.model.stream;

public class StreamUpdateParameters implements Validable {

  private String idStream;
  private String description;
  private String topic;
  private String title;
  private String photoIdMedia;
  private String readWriteMode;
  private String videoUrl;
  private boolean notifyPinMessage;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getPhotoIdMedia() {
    return photoIdMedia;
  }

  public void setPhotoIdMedia(String photoIdMedia) {
    this.photoIdMedia = photoIdMedia;
  }

  public String getReadWriteMode() {
    return readWriteMode;
  }

  public void setReadWriteMode(String readWriteMode) {
    this.readWriteMode = readWriteMode;
  }

  public boolean isNotifyMessage() {
    return notifyPinMessage;
  }

  public void setNotifyMessage(boolean notifyMessage) {
    this.notifyPinMessage = notifyMessage;
  }

  public String getIdStream() {
    return idStream;
  }

  public void setIdStream(String idStream) {
    this.idStream = idStream;
  }

  @Override public String getTitleToValidate() {
    return title;
  }

  @Override public String getDescriptionToValidate() {
    return description;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }
}
